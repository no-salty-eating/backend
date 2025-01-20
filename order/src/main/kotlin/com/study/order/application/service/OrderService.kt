package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.client.HistoryApi
import com.study.order.application.dto.CouponStatus
import com.study.order.application.dto.DiscountType
import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.dto.event.consumer.PaymentResultEvent
import com.study.order.application.dto.event.provider.CreateOrderEvent
import com.study.order.application.dto.event.provider.OrderHistoryEvent
import com.study.order.application.dto.event.provider.OrderSuccessEvent
import com.study.order.application.dto.request.CreateOrderRequestDto
import com.study.order.application.dto.response.ProductResponseDto
import com.study.order.application.exception.InvalidCouponException
import com.study.order.application.exception.InvalidCouponPriceException
import com.study.order.application.exception.NotEnoughStockException
import com.study.order.application.exception.NotFoundOrderException
import com.study.order.application.exception.NotFoundProductException
import com.study.order.application.messaging.MessageService
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderDetail
import com.study.order.domain.model.OrderStatus.ORDER_FINALIZED
import com.study.order.domain.model.OrderStatus.PAYMENT_FAILED
import com.study.order.domain.model.OrderStatus.PAYMENT_PROGRESS
import com.study.order.domain.repository.OrderDetailRepository
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.config.log.LoggerProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OrderService(
    private val historyApi: HistoryApi,
    private val cacheService: CacheService,
    private val couponService: CouponService,
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    companion object {
        private const val CREATE_ORDER = "create-order"
        private const val CREATE_ORDER_TEST = "create-order-test"
        private const val ORDER_SUCCESS = "order-success"
        private const val ORDER_SUCCESS_TEST = "order-success-test"
        private val logger = LoggerProvider.logger
    }

    @Transactional
    suspend fun create(request: CreateOrderRequestDto): Long? {

        val products = getProductInfo(request)

        validateAndDecrementStock(request, products)

        // TODO: 쓰기 / 읽기 DB를 분리하지 않으면 DB 부하 시 지연 / 타임아웃 등이 발생할 수 있음
        //  원본 : 쓰기 / 복제본 : 읽기
        //  데이터소스를 원본 / 복제본 모두에
        val order = saveOrder(request, products)

        val discountPrice = calculateDiscountPrice(request, products)

        // 여기서 장애가 발생한다면?  또는 kafka 가 종료되었다면? 주문 정보가 생성되고 메시지가 유실된경우?
        // 이 때, saveOrder 와 publishEvent 를 같은 트랜잭션에 묶는 방법
        // -> outBox 에 이벤트 정보를 저장
        val event = CreateOrderEvent(
            userId = request.userId,
            description = makeDescription(request, products),
            pgOrderId = order.pgOrderId,
            paymentPrice = order.totalPrice - discountPrice,
        )

        messageService.sendEvent(CREATE_ORDER_TEST, event)

        return order.id
    }

    @Transactional
    suspend fun updateOrderStatus(request: PaymentProcessingEvent) {
        val order = getOrderByPgOrderId(request.pgOrderId)
        order.updateStatus(PAYMENT_PROGRESS)

        orderRepository.save(order)
    }

    @Transactional
    suspend fun updateOrderStatus(request: PaymentResultEvent) {
        val order = getOrderByPgOrderId(request.pgOrderId)
        val orderDetails = getOrderDetailsByOrderId(order.id)

        if (request.pgStatus == "CAPTURE_SUCCESS") {

            val event = orderDetails.map {
                OrderSuccessEvent(
                    it.productId,
                    it.quantity,
                    it.userCouponId,
                    order.userId,
                )
            }
            messageService.sendEvent(ORDER_SUCCESS, event)
            order.updateStatus(ORDER_FINALIZED)
        } else {
            order.updateStatus(PAYMENT_FAILED)
        }

        orderDetails.map {
            cacheService.deleteOrderInfo(it.productId, cacheService.isTimeSaleOrder(it.productId))
            if (order.orderStatus == PAYMENT_FAILED)
                cacheService.incrementStock(it.productId, it.quantity, cacheService.isTimeSaleOrder(it.productId))
        }

        historyApi.save(OrderHistoryEvent.fromOrder(order, request.description))

        orderRepository.save(order)
    }

    private suspend fun getProductInfo(request: CreateOrderRequestDto): Map<Long, ProductResponseDto> {
        return request.products.map {
            cacheService.getProductInfo(it.productId) ?: throw NotFoundProductException()
        }.associateBy { it.productId }
    }

    private suspend fun validateAndDecrementStock(
        request: CreateOrderRequestDto,
        products: Map<Long, ProductResponseDto>
    ) {
        request.products.forEach {
            val product = products[it.productId]!!

            if (it.quantity > product.stock) {
                throw NotEnoughStockException()
            } else {
                cacheService.decrementStock(product.productId, it.quantity, product.isTimeSale)
            }
        }
    }

    private suspend fun calculateDiscountPrice(
        request: CreateOrderRequestDto,
        products: Map<Long, ProductResponseDto>
    ): Int {
        val productsByUserCouponId = request.products
            .filter { it.userCouponId != null }
            .associate { it.userCouponId!! to it.productId }

        if (productsByUserCouponId.isEmpty()) return 0

        val couponsById =
            couponService.getCouponList(request.userId, productsByUserCouponId.keys).associateBy { it.userCouponId }

        return couponsById.entries.sumOf { (couponId, coupon) ->
            val productId = productsByUserCouponId[couponId]
            val product = products[productId]!!

            if (product.price < coupon.minOrderAmount) throw InvalidCouponPriceException()
            if (coupon.status != CouponStatus.AVAILABLE.name) throw InvalidCouponException()

            when (coupon.discountType) {
                DiscountType.AMOUNT.name -> coupon.discountValue
                DiscountType.RATE.name -> minOf(
                    product.price * (coupon.discountValue / 100),
                    coupon.maxDiscountAmount
                )

                else -> throw InvalidCouponException()
            }
        }
    }

    private suspend fun saveOrder(
        request: CreateOrderRequestDto,
        products: Map<Long, ProductResponseDto>,
    ): Order {
        val totalPrice = request.products.sumOf { products[it.productId]!!.price * it.quantity }

        val newOrder = orderRepository.save(
            Order(
                userId = request.userId,
                totalPrice = totalPrice,
                pgOrderId = getPgOrderId(),
            )
        )

        request.products.forEach {
            orderDetailRepository.save(
                OrderDetail(
                    orderId = newOrder.id,
                    productId = it.productId,
                    userCouponId = it.userCouponId,
                    price = products[it.productId]!!.price,
                    quantity = it.quantity,
                )
            )
            cacheService.saveOrderInfo(it.productId, products[it.productId]!!.isTimeSale)
        }

        return newOrder
    }

    private suspend fun makeDescription(
        request: CreateOrderRequestDto,
        products: Map<Long, ProductResponseDto>
    ) = request.products.joinToString(", ") { "${products[it.productId]!!.name} x ${it.quantity}" }

    private suspend fun getPgOrderId() = "${UUID.randomUUID()}".replace("-", "")

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }

    private suspend fun getOrderDetailsByOrderId(orderId: Long): List<OrderDetail> {
        return orderDetailRepository.getOrderDetailsByOrderId(orderId)
    }
}
