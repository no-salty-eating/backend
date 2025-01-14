package com.study.history.infrastructure.repository

import com.study.history.application.service.OrderHistoryNativeService
import com.study.history.domain.model.History
import com.study.history.infrastructure.utils.criteria
import com.study.history.infrastructure.utils.sort
import com.study.history.infrastructure.utils.toLocalDate
import com.study.history.presentation.api.request.SearchQueryRequest
import com.study.history.presentation.api.response.SearchHistoryResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.stereotype.Repository

@Repository
class OrderHistoryNativeRepository(
    private val template: ReactiveElasticsearchTemplate,
) : OrderHistoryNativeService {

    companion object {
        private const val FORMAT = "yyyyMMdd"
    }

    override suspend fun search(request: SearchQueryRequest): SearchHistoryResponse? {
        val criteria = Criteria().apply {
            request.orderId?.let {
                and(History::orderId.criteria.`in`(it))
            }
            request.userId?.let {
                and(History::userId.criteria.`in`(it))
            }
            request.keyword?.split(" ")?.toSet()?.forEach {
                and(History::description.criteria.contains(it))
            }
            request.orderStatus?.let {
                and(History::orderStatus.criteria.`in`(it))
            }
            request.fromDate?.toLocalDate(FORMAT)?.atStartOfDay()?.let {
                and(History::createdAt.criteria.greaterThanEqual(it))
            }
            request.toDate?.toLocalDate(FORMAT)?.plusDays(1)?.atStartOfDay()?.let {
                and(History::createdAt.criteria.lessThan(it))
            }
        }

        val query = CriteriaQuery(criteria, PageRequest.of(0, request.pageSize)).apply {
            sort = History::createdAt.sort(DESC)
            searchAfter = request.pageNext
        }

        return template.searchForPage(query, History::class.java).awaitSingle().let { response ->
            SearchHistoryResponse(
                response.content.map { it.content },
                response.totalElements,
                response.content.lastOrNull()?.sortValues
            )
        }
    }
}