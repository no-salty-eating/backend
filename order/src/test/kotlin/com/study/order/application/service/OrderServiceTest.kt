package com.study.order.application.service

import com.study.order.application.client.CouponService
import com.study.order.application.dto.request.CreateOrderRequestDto
import com.study.order.application.dto.request.ProductQuantityRequestDto
import com.study.order.application.dto.response.CouponResponse
import com.study.order.application.messaging.MessageService
import com.study.order.infrastructure.config.log.LoggerProvider
import com.study.order.infrastructure.repository.OrderRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

private val logger = LoggerProvider.logger

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest(
    @Autowired orderService: OrderService,
    @Autowired couponService: CouponService,
    @Autowired messageService: MessageService,
    @Autowired orderRepository: OrderRepository,
) : WithReisContainer, StringSpec({

    "create without cache" {
        val request = CreateOrderRequestDto(
            1, listOf(
                ProductQuantityRequestDto(1, 1, 1234),
                ProductQuantityRequestDto(2, 2, 4321),
            )
        )

        Mockito.`when`(couponService.getCouponList(1, setOf(1234, 4321))).thenReturn(
            listOf(
                CouponResponse(1234, "AMOUNT", 1000, 500, 1000, "AVAILABLE"),
                CouponResponse(4321, "AMOUNT", 500, 0, 500, "AVAILABLE"),
            )
        )
        Mockito.`when`(messageService.sendEvent(anyString(), anyString())).thenReturn(null)
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
    fun testCouponService(): CouponService {
        return Mockito.mock(CouponService::class.java)
    }

    @Bean
    @Primary
    fun testMessageService(): MessageService {
        return Mockito.mock(MessageService::class.java)
    }
}

interface WithReisContainer {
    companion object {
        private val container = GenericContainer(DockerImageName.parse("redis")).apply {
            addExposedPorts(6379)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun setProperty(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.port") {
                "${container.getMappedPort(6379)}"
            }
        }
    }
}