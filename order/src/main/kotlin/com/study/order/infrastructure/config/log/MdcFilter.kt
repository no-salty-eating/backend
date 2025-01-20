package com.study.order.infrastructure.config.log

import com.study.order.infrastructure.utils.txid
import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*

const val KEY_TXID = "X-Id"

@Order(1)
@Component
class MdcFilter : WebFilter {

    init {
        propagateMdcThroughReactor()
    }

    // 마스터 - 슬레이브 구조로 변경 -> 락 제거 후 슬레이브를 (대기열)큐로 전환해서 redis batch 를 이용해 쓰기작업을 마스터에서 작업하도록 변경?
    private fun propagateMdcThroughReactor() {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(
            KEY_TXID,
            { MDC.get(KEY_TXID) },
            { value -> MDC.put(KEY_TXID, value) },
            { MDC.remove(KEY_TXID) }
        )
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val txid = exchange.request.headers[KEY_TXID]?.firstOrNull() ?: "${UUID.randomUUID()}"

        MDC.put(KEY_TXID, txid)

        return chain.filter(exchange).contextWrite {
            Context.of(KEY_TXID, txid)
        }.doOnError {
            exchange.request.txid = txid
        }
    }
}