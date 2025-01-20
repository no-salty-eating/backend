package com.study.payment.application.service

import com.study.payment.application.client.PaymentApiService
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.dto.event.provider.PaymentProcessingEvent
import com.study.payment.application.dto.event.provider.PaymentResultEvent
import com.study.payment.application.exception.InvalidPaymentPriceException
import com.study.payment.application.exception.NotFoundPaymentException
import com.study.payment.application.exception.TooManyPaymentRequestException
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Service
class PaymentTestService(
    private val cacheService: CacheService,
    private val kafkaProducer: KafkaMessagePublisher,
    private val paymentRepository: PaymentRepository,
    private val transactionHelper: TransactionHelper,
    private val paymentApiService: PaymentApiService,
) {

    companion object {
        private const val PAYMENT_PROCESSING_TEST = "payment-processing-test"
        private const val PAYMENT_RESULT_TEST = "payment-result-test"
        private val logger = LoggerProvider.logger
    }

    @Transactional
    suspend fun createPaymentInfoTest(createOrderEvent: CreateOrderEvent) {
        val payment = paymentRepository.save(
            Payment(
                userId = createOrderEvent.userId,
                description = createOrderEvent.description,
                paymentPrice = createOrderEvent.paymentPrice,
                pgOrderId = createOrderEvent.pgOrderId,
            )
        )

        kafkaProducer.sendEvent(PAYMENT_PROCESSING_TEST, payment.pgOrderId?.let {
            PaymentProcessingEvent(
                payment.id,
                it
            )
        })
    }

    @Transactional
    suspend fun retryRequestPayment(paymentId: Long) {
        getPayment(paymentId).let {
            delay(getDelay(it))
            keyInjection(paymentId)
        }
    }

    @Transactional
    suspend fun keyInjection(paymentId: Long) {
        val payment = getPayment(paymentId).apply {
            this.injectionPgKey(makePgKey())
            this.updateStatus(AUTH_SUCCESS)
        }

        capture(payment)

        if (makeRandom()) {
            payment.updateStatus(AUTH_INVALID)
            capture(payment)
            throw InvalidPaymentPriceException()
        }

        payment.updateStatus(CAPTURE_REQUEST)
        payment.increaseRetryCount()
        cacheService.put(payment.id)

        capture(payment)

        if (makeRandom()) {

            if (payment.pgRetryCount >= 3) {
                payment.updateStatus(CAPTURE_FAIL)
                capture(payment)
                throw TooManyPaymentRequestException()
            }

            payment.updateStatus(CAPTURE_RETRY)

            capture(payment)
            paymentApiService.retry(payment.id)

            return
        }

        if (makeRandom()) {
            payment.updateStatus(CAPTURE_FAIL)
        } else {
            payment.updateStatus(CAPTURE_SUCCESS)
        }

        capture(payment)
        cacheService.remove(payment.id)
        kafkaProducer.sendEvent(
            PAYMENT_RESULT_TEST, PaymentResultEvent(
                payment.pgOrderId!!,
                payment.paymentPrice,
                payment.pgStatus.name,
                payment.description!!,
            )
        )
    }

    private suspend fun getPayment(paymentId: Long): Payment {
        return paymentRepository.findById(paymentId) ?: throw NotFoundPaymentException()
    }

    private fun makePgKey(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val randomString = (1..5)
            .map { chars.random() }
            .joinToString("")

        return "tgen_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}$randomString"
    }

    private fun makeRandom(): Boolean {
        return Random.nextInt(100) < 10
    }

    private suspend fun capture(payment: Payment) {
        transactionHelper.executeInNewTransaction {
            paymentRepository.save(payment)
        }
    }

    private fun getDelay(payment: Payment): Duration {
        val temp = (2.0).pow(payment.pgRetryCount).toInt() * 1000
        val delay = temp + (0..temp).random()
        return delay.milliseconds
    }
}