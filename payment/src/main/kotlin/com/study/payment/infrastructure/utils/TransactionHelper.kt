package com.study.payment.infrastructure.utils

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionHelper {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun executeInNewTransaction(runner: suspend () -> Unit) {
        runner()
    }
}