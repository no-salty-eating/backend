package com.study.payment

import com.study.payment.application.service.PaymentService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@EnableR2dbcAuditing
@SpringBootApplication
class PaymentApplication(
    private val paymentService: PaymentService
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        runBlocking {
            paymentService.reTryOnBoot()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}
