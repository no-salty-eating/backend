package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.client.PointService
import com.study.order.application.client.ProductService
import com.study.order.application.dto.response.CategoryResponse
import com.study.order.application.dto.response.CouponResponse
import com.study.order.application.dto.response.ProductResponse
import com.study.order.application.messaging.MessageService
import com.study.order.infrastructure.config.log.LoggerProvider
import com.study.order.infrastructure.repository.OrderRepository
import com.study.order.presentation.api.request.CreateOrderRequest
import com.study.order.presentation.api.request.ProductQuantityRequest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

private val logger = LoggerProvider.logger

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest(
    @Autowired pointService: PointService,
    @Autowired couponService: CouponService,
    @Autowired productService: ProductService,
    @Autowired messageService: MessageService,
    @Autowired orderService: OrderService,
    @Autowired orderRepository: OrderRepository,
) : StringSpec({


    "create" {
        val request = CreateOrderRequest(
            1, 1000, listOf(
                ProductQuantityRequest(1, 1, 1234),
                ProductQuantityRequest(2, 2, 4321),
            )
        )

        Mockito.`when`(pointService.validateUserPoints(1, 1000)).thenReturn(true)
        Mockito.`when`(productService.getProductList(setOf(1, 2))).thenReturn(listOf(
                ProductResponse(1, "apple", 1000, 100, listOf(CategoryResponse(1, "fruit"))),
                ProductResponse(2, "banana", 2000, 100, listOf(
                        CategoryResponse(1, "fruit"),
                        CategoryResponse(2, "yellow"),
                    )
                ),
            ))
        Mockito.`when`(couponService.getCouponList(setOf(1234, 4321))).thenReturn(listOf(
                CouponResponse(1234, 1, 500, null, 0, "AVAILABLE"),
                CouponResponse(4321, 2, null, 10, 0, "AVAILABLE"),
            ))
        val orderId = orderService.create(request)

        orderRepository.findById(orderId!!).let { order ->
            logger.debug { ">> order : $order " }
            orderId shouldNotBe null
        }
    }

})

@Configuration
@Profile("test")
class TestWebClientConfig {

    @Bean
    @Primary
    fun testPointService(): PointService {
        return Mockito.mock(PointService::class.java)
    }

    @Bean
    @Primary
    fun testCouponService(): CouponService {
        return Mockito.mock(CouponService::class.java)
    }

    @Bean
    @Primary
    fun testProductService(): ProductService {
        return Mockito.mock(ProductService::class.java)
    }
}