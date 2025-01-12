package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.dto.CouponStatus
import com.study.order.application.dto.DiscountType
import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.dto.event.consumer.PaymentResultEvent
import com.study.order.application.dto.event.provider.CreateOrderEvent
import com.study.order.application.dto.event.provider.OrderSuccessEvent
import com.study.order.application.dto.event.provider.ProductStockAdjustmentEvent
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

private const val CREATE_ORDER = "orchestrator:create-order"
private const val PRODUCT_STOCK_ADJUSTMENT = "orchestrator:product-stock-adjustment"
private const val ORDER_SUCCESS = "orchestrator:order-success"

@Service
class OrderService(
    private val cacheService: CacheService,
    private val couponService: CouponService,
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    private val logger = LoggerProvider.logger

    @Transactional
    suspend fun create(request: CreateOrderRequestDto): Long? {

        val products = getProductInfo(request)

        validateAndIncrementStock(request, products)

        val discountPrice = calculateDiscountPrice(request, products)

        val order = saveOrder(request, products)

        publishEvents(request, order, products, discountPrice)

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
                    it.userCouponId
                )
            }
            messageService.sendEvent(ORDER_SUCCESS, event)
            order.updateStatus(ORDER_FINALIZED)
        } else {
            val event = orderDetails.map {
                cacheService.decrement(it.productId, it.quantity)
                ProductStockAdjustmentEvent(
                    it.productId,
                    it.quantity,
                    false
                )
            }
            sendProductStockEvent(event)
            order.updateStatus(PAYMENT_FAILED)
        }

        orderRepository.save(order)
    }

    private suspend fun getProductInfo(request: CreateOrderRequestDto): Map<Long, ProductResponseDto> {
        return request.products.map {
            cacheService.getProductInfo(it.productId) ?: throw NotFoundProductException()
        }.associateBy { it.productId }
    }

    private suspend fun validateAndIncrementStock(
        request: CreateOrderRequestDto,
        products: Map<Long, ProductResponseDto>
    ) {
        request.products.forEach {
            val product = products[it.productId]!!
            cacheService.getSoldQuantity(it.productId)?.let { soldQuantity ->
                if (soldQuantity + it.quantity > product.stock) {
                    throw NotEnoughStockException()
                } else {
                    cacheService.increment(it.productId, it.quantity)
                }
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

        val couponsById = couponService.getCouponList(request.userId, productsByUserCouponId.keys).associateBy { it.id }

        return couponsById.entries.sumOf { (couponId, coupon) ->
            val productId = productsByUserCouponId[couponId]
            val product = products[productId]!!

            if (product.price < coupon.minOrderAmount) throw InvalidCouponPriceException()
            if (coupon.couponStatus != CouponStatus.AVAILABLE.name) throw InvalidCouponException()

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
                pgOrderId = "${UUID.randomUUID()}".replace("-", ""),
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
        }

        return newOrder
    }

    private suspend fun publishEvents(
        request: CreateOrderRequestDto,
        newOrder: Order,
        products: Map<Long, ProductResponseDto>,
        discountPrice: Int
    ) {
        sendProductStockEvent(request.products.map {
            ProductStockAdjustmentEvent(
                it.productId,
                it.quantity,
                true
            )
        })

        messageService.sendEvent(
            CREATE_ORDER, CreateOrderEvent(
                userId = request.userId,
                description = request.products.joinToString(", ") { "${products[it.productId]!!.name} x ${it.quantity}" },
                pgOrderId = newOrder.pgOrderId!!,
                paymentPrice = newOrder.totalPrice - discountPrice,
            )
        )
    }

    private suspend fun sendProductStockEvent(event : List<ProductStockAdjustmentEvent>) {
        messageService.sendEvent(PRODUCT_STOCK_ADJUSTMENT, event)
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }

    private suspend fun getOrderDetailsByOrderId(orderId: Long): List<OrderDetail> {
        return orderDetailRepository.getOrderDetailsByOrderId(orderId)
    }
}
