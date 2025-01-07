package com.study.order

import com.study.order.domain.model.Order
import com.study.order.infrastructure.repository.OrderRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class OrderApplicationTests(
    @Autowired orderRepository: OrderRepository,
) : StringSpec({

    "order" {
        val prevCount = orderRepository.count()

        orderRepository.save(Order(userId = 1)).also { logger.debug { it } }

        val currCount = orderRepository.count()

        currCount shouldBe prevCount + 1
        orderRepository.deleteAll()
    }

})
