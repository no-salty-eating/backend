package com.study.payment.infrastructure.utils

import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Component
class TransactionHelper(
    private val transactionalOperator: TransactionalOperator
) {

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun executeInNewTransaction(runner: suspend () -> Unit) {
        transactionalOperator.executeAndAwait {
            runner()
        }
    }
}