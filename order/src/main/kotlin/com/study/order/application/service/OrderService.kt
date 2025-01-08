package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.client.PointService
import com.study.order.application.client.ProductService
import com.study.order.application.dto.event.PaymentEvent
import com.study.order.application.dto.event.ProductStockDecreaseEvent
import com.study.order.application.dto.response.ProductResponse
import com.study.order.application.exception.InvalidCouponCategoryException
import com.study.order.application.exception.InvalidCouponException
import com.study.order.application.exception.InvalidCouponPriceException
import com.study.order.application.exception.NotEnoughPointException
import com.study.order.application.exception.NotEnoughStockException
import com.study.order.application.exception.NotFoundProductException
import com.study.order.application.messaging.MessageService
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderDetail
import com.study.order.domain.repository.OrderDetailRepository
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.config.log.LoggerProvider
import com.study.order.presentation.api.request.CreateOrderRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OrderService(
    private val pointService: PointService,
    private val couponService: CouponService,
    private val productService: ProductService,
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    private val logger = LoggerProvider.logger

    @Transactional
    suspend fun create(request: CreateOrderRequest): Long? {

        if (request.pointAmount > 0)
            if (!pointService.validateUserPoints(request.userId, request.pointAmount))
                throw NotEnoughPointException()

        val productIds = request.products.map { it.productId }.toSet()
        val productsByCouponId = request.products
            .filter { it.couponId != null }
            .associate {
                it.couponId!! to it.productId
            }

        val couponIds = productsByCouponId.keys
        val productsById = productService.getProductList(productIds).associateBy { it.id }

        if (request.products.any {
                val product = productsById[it.productId] ?: throw NotFoundProductException()
                it.quantity > product.stock
            }) {
            throw NotEnoughStockException()
        }

        val discountPrice = if (couponIds.isNotEmpty()) {
            val couponsById = couponService.getCouponList(couponIds).associateBy { it.id }

            couponsById.entries.sumOf { (couponId, coupon) ->

                val productId = productsByCouponId[couponId]
                val product: ProductResponse = productsById[productId]!!

                if (!product.categoryList.any { category -> category.id == coupon.useCategoryId }) {
                    throw InvalidCouponCategoryException()
                }

                if (product.price < coupon.availablePrice) {
                    throw InvalidCouponPriceException()
                }

                if (coupon.couponStatus != "AVAILABLE") {
                    throw InvalidCouponException()
                }

                when {
                    coupon.discountRate != null -> product.price * (coupon.discountRate / 100)
                    coupon.discountPrice != null -> coupon.discountPrice
                    else -> throw InvalidCouponException()
                }
            }
        } else 0

        val totalPrice = request.products.sumOf { productsById[it.productId]!!.price * it.quantity }
        val description =
            request.products.joinToString(", ") { "${productsById[it.productId]!!.name} x ${it.quantity}" }

        val newOrder = orderRepository.save(
            Order(
                userId = request.userId,
                description = description,
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
                    price = productsById[it.productId]!!.price,
                    quantity = it.quantity,
                )
            )
        }

        if (couponIds.isNotEmpty())
            messageService.sendEvent("coupon-used", couponIds)

        messageService.sendEvent("product-stock-decrease", request.products.map {
            ProductStockDecreaseEvent(
                it.productId,
                it.quantity
            )
        })

        messageService.sendEvent("payment", PaymentEvent(
                request.userId,
                newOrder.pgOrderId!!,
                newOrder.totalPrice - discountPrice - request.pointAmount
            )
        )

        return newOrder.id
    }

}
