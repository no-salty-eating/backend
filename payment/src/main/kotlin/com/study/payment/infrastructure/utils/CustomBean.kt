package com.study.payment.infrastructure.utils

import com.study.payment.application.service.PaymentService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class CustomBean : ApplicationContextAware {
    companion object {
        lateinit var ctx: ApplicationContext
            private set

        private fun <T : Any> getBean(byClass: KClass<T>, vararg arg: Any) : T {
            return ctx.getBean(byClass.java, arg)
        }

        val paymentService: PaymentService by lazy { getBean(PaymentService::class) }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }
}