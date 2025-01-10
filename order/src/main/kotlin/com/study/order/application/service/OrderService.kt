package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.client.ProductService
import com.study.order.application.dto.event.CreateOrderEvent
import com.study.order.application.dto.event.ProductStockDecreaseEvent
import com.study.order.application.dto.request.CreateOrderRequestDto
import com.study.order.application.dto.response.ProductResponse
import com.study.order.application.exception.InvalidCouponException
import com.study.order.application.exception.InvalidCouponPriceException
import com.study.order.application.exception.NotEnoughStockException
import com.study.order.application.exception.NotFoundProductException
import com.study.order.application.messaging.MessageService
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderDetail
import com.study.order.domain.repository.OrderDetailRepository
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.config.log.LoggerProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private const val CREATE_ORDER = "create-order"
private const val COUPON_USED = "coupon-used"
private const val PRODUCT_STOCK_DECREASE = "product-stock-decrease"

@Service
class OrderService(
//    private val couponService: CouponService,
//    private val productService: ProductService,
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    private val logger = LoggerProvider.logger

    @Transactional
    suspend fun create(request: CreateOrderRequestDto): Long? {

//        val productIds = request.products.map { it.productId }.toSet()
//        val productsByCouponId = request.products
//            .filter { it.couponId != null }
//            .associate {
//                it.couponId!! to it.productId
//            }

//        val couponIds = productsByCouponId.keys
//        val productsById = productService.getProductList(productIds).associateBy { it.id }
//
//        if (request.products.any {
//                val product = productsById[it.productId] ?: throw NotFoundProductException()
//                it.quantity > product.stock
//            }) {
//            throw NotEnoughStockException()
//        }

//        val discountPrice = if (couponIds.isNotEmpty()) {
//            val couponsById = couponService.getCouponList(couponIds).associateBy { it.id }
//
//            couponsById.entries.sumOf { (couponId, coupon) ->
//
//                val productId = productsByCouponId[couponId]
//                val product: ProductResponse = productsById[productId]!!
//
//                if (product.price < coupon.minOrderAmount) {
//                    throw InvalidCouponPriceException()
//                }
//
//                if (coupon.couponStatus != "AVAILABLE") {
//                    throw InvalidCouponException()
//                }
//
//                when (coupon.discountType) {
//                    "AMOUNT" -> coupon.discountValue
//                    "RATE" -> if (product.price * (coupon.discountValue / 100) > coupon.maxDiscountAmount)
//                        coupon.maxDiscountAmount
//                    else product.price * (coupon.discountValue / 100)
//
//                    else -> throw InvalidCouponException()
//                }
//            }
//        } else 0

//        val totalPrice = request.products.sumOf { productsById[it.productId]!!.price * it.quantity }
//        val description =
//            request.products.joinToString(", ") { "${productsById[it.productId]!!.name} x ${it.quantity}" }

        val newOrder = orderRepository.save(
            Order(
                userId = request.userId,
//                totalPrice = totalPrice,
                totalPrice = 2000,
                pgOrderId = "${UUID.randomUUID()}".replace("-", ""),
            )
        )

        request.products.forEach {
            orderDetailRepository.save(
                OrderDetail(
                    orderId = newOrder.id,
                    productId = it.productId,
                    couponId = it.couponId,
//                    price = productsById[it.productId]!!.price,
                    price = 2000,
                    quantity = it.quantity,
                )
            )
        }

//        if (couponIds.isNotEmpty())
//            messageService.sendEvent(COUPON_USED, couponIds)

//        messageService.sendEvent(PRODUCT_STOCK_DECREASE, request.products.map {
//            ProductStockDecreaseEvent(
//                it.productId,
//                it.quantity
//            )
//        })

        messageService.sendEvent(
            CREATE_ORDER, CreateOrderEvent(
                request.userId,
//                description,
                "으하하",
                newOrder.pgOrderId!!,
//                newOrder.totalPrice - discountPrice
                newOrder.totalPrice
            )
        )

        return newOrder.id
    }

}
