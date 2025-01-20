package com.study.payment.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.client.PaymentApiService
import com.study.payment.application.client.TossPayService
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.dto.event.provider.PaymentProcessingEvent
import com.study.payment.application.dto.event.provider.PaymentResultEvent
import com.study.payment.application.dto.request.PaySucceedRequestDto
import com.study.payment.application.exception.InvalidPaymentPriceException
import com.study.payment.application.exception.InvalidPaymentStatusException
import com.study.payment.application.exception.NotFoundPaymentException
import com.study.payment.application.exception.TooManyPaymentRequestException
import com.study.payment.application.exception.TossApiError
import com.study.payment.domain.model.Payment
import com.study.payment.domain.model.PgStatus.AUTH_INVALID
import com.study.payment.domain.model.PgStatus.AUTH_SUCCESS
import com.study.payment.domain.model.PgStatus.CAPTURE_FAIL
import com.study.payment.domain.model.PgStatus.CAPTURE_REQUEST
import com.study.payment.domain.model.PgStatus.CAPTURE_RETRY
import com.study.payment.domain.model.PgStatus.CAPTURE_SUCCESS
import com.study.payment.domain.repository.PaymentRepository
import com.study.payment.infrastructure.config.log.LoggerProvider
import com.study.payment.infrastructure.messaging.provider.KafkaMessagePublisher
import com.study.payment.infrastructure.utils.TransactionHelper
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import java.time.Duration as JavaDuration

@Service
class PaymentService(
    private val mapper: ObjectMapper,
    private val cacheService: CacheService,
    private val tossPayApi: TossPayService,
    private val kafkaProducer: KafkaMessagePublisher,
    private val paymentRepository: PaymentRepository,
    private val transactionHelper: TransactionHelper,
    private val paymentApiService: PaymentApiService,
) {

    companion object {
        private const val PAYMENT_PROCESSING = "payment-processing"
        private const val PAYMENT_RESULT = "payment-result"
        private val logger = LoggerProvider.logger
    }

    @Transactional(readOnly = true)
    suspend fun getPaymentInfo(pgOrderId: String): Payment {

        val payment = getOrderByPgOrderId(pgOrderId)

        kafkaProducer.sendEvent(PAYMENT_PROCESSING, payment.pgOrderId?.let {
            PaymentProcessingEvent(
                payment.id,
                it
            )
        })

        return payment
    }

    suspend fun reTryOnBoot() {
        cacheService.getAll()
            .apply {
                if (this.isEmpty())
                    return
                else {
                    this.let {
                        getPayments(it).sortedBy { payment -> payment.updatedAt }
                    }.filter {
                        JavaDuration.between(it.updatedAt!!, LocalDateTime.now()).seconds >= 60
                    }.forEach {
                        cacheService.remove(it.id)
                        paymentApiService.retry(it.id)
                    }
                }
            }
    }

    @Transactional
    suspend fun createPaymentInfo(createOrderEvent: CreateOrderEvent) {
        paymentRepository.save(
            Payment(
                userId = createOrderEvent.userId,
                description = createOrderEvent.description,
                paymentPrice = createOrderEvent.paymentPrice,
                pgOrderId = createOrderEvent.pgOrderId,
            )
        )
    }

    @Transactional
    suspend fun createPaymentInfoTest(createOrderEvent: CreateOrderEvent) {
        paymentRepository.save(
            Payment(
                userId = createOrderEvent.userId,
                description = createOrderEvent.description,
                paymentPrice = createOrderEvent.paymentPrice,
                pgOrderId = createOrderEvent.pgOrderId,
            )
        )
    }

    @Transactional
    suspend fun paymentKeyInjection(request: PaySucceedRequestDto): Boolean {
        val payment = getOrderByPgOrderId(request.orderId).apply {
            this.injectionPgKey(request.paymentKey)
            this.updateStatus(AUTH_SUCCESS)
        }

        try {
            return if (payment.paymentPrice != request.amount) {
                payment.updateStatus(AUTH_INVALID)
                logger.debug { "${payment.userId}번 사용자 결제 시도 : 결제 금액이 다름 (Payment : ${payment.paymentPrice} , Order : ${request.amount}) " }
                throw InvalidPaymentPriceException()
            } else {
                true
            }
        } finally {
            paymentRepository.save(payment)
        }
    }

    @Transactional
    suspend fun requestPayment(request: PaySucceedRequestDto) {
        val payment = getOrderByPgOrderId(request.orderId).apply {
            this.updateStatus(CAPTURE_REQUEST)
            transactionHelper.executeInNewTransaction {
                paymentRepository.save(this)
            }
        }

        requestPayment(payment)
    }

    @Transactional
    suspend fun requestPayment(payment: Payment) {

        if (payment.pgStatus !in setOf(CAPTURE_REQUEST, CAPTURE_RETRY)) {
            throw InvalidPaymentStatusException()
        }

        payment.increaseRetryCount()
        cacheService.put(payment.id)

        try {
            // TODO: 이걸 사용할 일이 있을까? / userId 가 2자 이상이어야 실행가능함
            tossPayApi.confirm(PaySucceedRequestDto.from(payment))
                .let { logger.debug { " >> 결제정보 : $it" } } // 이거 몽고디비로 던지면 될까
            payment.updateStatus(CAPTURE_SUCCESS)
        } catch (e: Exception) {

            // 결제 특성 상 결제 이력은 DB에 저장해두어야 함 ( retry 나 fail 전부 )
            // 결제 이력 테이블 만들어야함
            // 부하가 select + update < insert
            // 언제 시도 -> 왜 실패?

            /*
            * 이벤트 발행 시 A / B / C / D 에 데이터가 생기는 경우
            * -> 중앙에서 nosql 에 그냥 관리하는 경우도 있음 ( 변경될 여지가 없는 경우 )
            * 카프카 리밸런싱?
            * -> 컨슈머로 인식을 한 경우 한번씩 헬스체크 함 -> 대답안함 -> 컨슈머를 붙였다 뗐다 함..?
            * -> 수신(소비) 속도보다 작업 속도가 느린경우 -> 컨슈머를 제외해버림 !
            * */
            logger.error(e.message, e)
            payment.updateStatus(
                when (e) {
                    is WebClientRequestException -> CAPTURE_RETRY
                    is WebClientResponseException -> {
                        when (e.toTossPayApiError().code) {
                            "ALREADY_PROCESSED_PAYMENT" -> CAPTURE_SUCCESS
                            "PROVIDER_ERROR", "FAILED_INTERVAL_SYSTEM_PROCESSING" -> CAPTURE_RETRY
                            else -> CAPTURE_FAIL
                        }
                    }

                    else -> CAPTURE_FAIL
                }
            )

            if (payment.pgRetryCount >= 3) {
                payment.updateStatus(CAPTURE_FAIL)
                throw TooManyPaymentRequestException()
            }
        } finally {
            transactionHelper.executeInNewTransaction {
                paymentRepository.save(payment)
            }
            cacheService.remove(payment.id)

            if (payment.pgStatus == CAPTURE_RETRY) {
                paymentApiService.retry(payment.id)
            }
            if (payment.pgStatus == CAPTURE_SUCCESS || payment.pgStatus == CAPTURE_FAIL) {
                kafkaProducer.sendEvent(
                    PAYMENT_RESULT, PaymentResultEvent(
                        payment.pgOrderId!!,
                        payment.paymentPrice,
                        payment.pgStatus.name,
                        payment.description!!,
                    )
                )
            }
        }
    }

    @Transactional
    suspend fun retryRequestPayment(paymentId: Long) {
        getPayment(paymentId).let {
            delay(getDelay(it))
            requestPayment(it)
        }
    }

    private suspend fun getPayments(paymentIds: List<Long>): List<Payment> {
        return paymentRepository.findAllById(paymentIds)
    }

    private fun getDelay(payment: Payment): Duration {
        val temp = (2.0).pow(payment.pgRetryCount).toInt() * 1000
        val delay = temp + (0..temp).random()
        return delay.milliseconds
    }

    private fun WebClientResponseException.toTossPayApiError(): TossApiError {
        val json = String(this.responseBodyAsByteArray)
        return mapper.readValue(json, TossApiError::class.java)
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Payment {
        return paymentRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundPaymentException()
    }

    private suspend fun getPayment(paymentId: Long): Payment {
        return paymentRepository.findById(paymentId) ?: throw NotFoundPaymentException()
    }
}