package com.study.order.infrastructure.utils

import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Component
class TransactionHelper {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun executeInNewTransaction(timeoutSecond: Long = -1, runner: suspend () -> Unit) {
        if (timeoutSecond == -1L) {
            runner()
        }

        try {
            withTimeout(timeoutSecond.toDuration(DurationUnit.SECONDS)) {
                runner()
            }
        } catch (ex: Exception) {
            throw ex
        }
    }
}