package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.client.ProductService
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
private const val PRODUCT_STOCK_ADJUSTMENT = "product-stock-adjustment"
private const val ORDER_SUCCESS = "order-success"

@Service
class OrderService(
    private val cacheService: CacheService,
    private val couponService: CouponService,
    private val productService: ProductService,
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    private val logger = LoggerProvider.logger

    @Transactional
    suspend fun create(request: CreateOrderRequestDto): Long? {

        val products = request.products.map {
            cacheService.get(it.productId)
                ?: productService.getProduct(it.productId)
                ?: throw NotFoundProductException()
        }.associateBy { it.productId }

        val productsByCouponId = request.products
            .filter { it.couponId != null }
            .associate { it.couponId!! to it.productId }

        request.products.forEach {
            val product = products[it.productId]!!

            if (product.stock < it.quantity) {
                throw NotEnoughStockException()
            } else {
                // 여기 타임세일 상품인지 아닌지 구분해야함;
                for (i in 1..it.quantity) {
                    cacheService.increment(it.productId)
                }

                val size = cacheService.getSize(it.productId)

                if (size != null) {
                    if (size > product.stock) {
                        for (i in 1..it.quantity)
                            cacheService.decrement(it.productId)
                        throw NotEnoughStockException()
                    }
                }
            }
        }

        val discountPrice = if (productsByCouponId.keys.isNotEmpty()) {
            val couponsById = couponService.getCouponList(request.userId, productsByCouponId.keys).associateBy { it.id }

            couponsById.entries.sumOf { (couponId, coupon) ->

                val productId = productsByCouponId[couponId]
                val product: ProductResponseDto = products[productId]!!

                if (product.price < coupon.minOrderAmount) {
                    throw InvalidCouponPriceException()
                }

                if (coupon.couponStatus != "AVAILABLE") {
                    throw InvalidCouponException()
                }

                when (coupon.discountType) {
                    "AMOUNT" -> coupon.discountValue
                    "RATE" -> if (product.price * (coupon.discountValue / 100) > coupon.maxDiscountAmount)
                        coupon.maxDiscountAmount
                    else product.price * (coupon.discountValue / 100)

                    else -> throw InvalidCouponException()
                }
            }
        } else 0

        val totalPrice = request.products.sumOf { products[it.productId]!!.price * it.quantity }
        val description =
            request.products.joinToString(", ") { "${products[it.productId]!!.name} x ${it.quantity}" }

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
                    couponId = it.couponId,
                    price = products[it.productId]!!.price,
                    quantity = it.quantity,
                )
            )
        }

        messageService.sendEvent(PRODUCT_STOCK_ADJUSTMENT, request.products.map {
            ProductStockAdjustmentEvent(
                it.productId,
                it.quantity,
                true
            )
        })

        // 트랜잭셔널 아웃박스 패턴?
        messageService.sendEvent(
            CREATE_ORDER, CreateOrderEvent(
                request.userId,
                description,
                newOrder.pgOrderId!!,
                newOrder.totalPrice - discountPrice
            )
        )

        return newOrder.id
    }

    @Transactional
    suspend fun updateOrderStatus(request: PaymentProcessingEvent) {
        getOrderByPgOrderId(request.pgOrderId).updateStatus(PAYMENT_PROGRESS)
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
                    it.couponId
                )
            }
            messageService.sendEvent(ORDER_SUCCESS, event)
            order.updateStatus(ORDER_FINALIZED)
        } else {
            val event = orderDetails.map {
                ProductStockAdjustmentEvent(
                    it.productId,
                    it.quantity,
                    false
                )
                for (i in 1..it.quantity)
                    cacheService.decrement(it.productId)
            }
            messageService.sendEvent(PRODUCT_STOCK_ADJUSTMENT, event)
            order.updateStatus(PAYMENT_FAILED)
        }

        orderRepository.save(order)
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }

    private suspend fun getOrderDetailsByOrderId(orderId: Long): List<OrderDetail> {
        return orderDetailRepository.getOrderDetailsByOrderId(orderId)
    }
}
