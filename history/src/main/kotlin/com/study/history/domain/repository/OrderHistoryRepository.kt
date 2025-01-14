package com.study.history.domain.repository

import com.study.history.domain.model.History
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderHistoryRepository : CoroutineCrudRepository<History, Long> {

}
