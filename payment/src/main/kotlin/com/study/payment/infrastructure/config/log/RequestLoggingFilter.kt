package com.study.payment.infrastructure.config.log

import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels

@Component
@Order(11)
class RequestLoggingFilter : WebFilter {

    private val logger = LoggerProvider.logger

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val request = exchange.request
        if (request.method == HttpMethod.POST || request.method == HttpMethod.PATCH) {

            logger.info { "uri : [${request.method}] ${request.path}, ip: ${request.remoteAddress}" }

            if (request.headers.isNotEmpty())
                logger.info { "header : ${request.headers}" }
            if (request.queryParams.isNotEmpty())
                logger.info { "query : ${request.queryParams}" }

            val loggingRequest = object : ServerHttpRequestDecorator(request) {
                override fun getBody(): Flux<DataBuffer> {
                    return super.getBody().doOnNext { buffer ->
                        ByteArrayOutputStream().use { output ->
                            Channels.newChannel(output).write(buffer.readableByteBuffers().next())
                            String(output.toByteArray())
                        }.let { requestBody ->
                            logger.info { "payload: $requestBody" }
                        }
                    }
                }
            }

            return chain.filter(
                exchange.mutate().request(loggingRequest).build()
            )
        } else {
            return chain.filter(exchange)
        }
    }
}