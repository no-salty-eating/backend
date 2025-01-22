package com.study.order.infrastructure.config.webClient

import com.study.order.infrastructure.config.log.LoggerProvider
import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig(
    @Value("\${history.service.url}")
    private val history: String,
    @Value("\${coupon.service.url}")
    private val coupon: String,
) {

    companion object {
        private val logger = LoggerProvider.logger
    }

    @Bean
    fun couponServiceWebClient(): WebClient {
        logger.debug { ">> coupon service url : $coupon" }
        return createWebClient(coupon, "coupon-service")
    }

    @Bean
    fun historyServiceWebClient(): WebClient {
        logger.debug { ">> history service url : $history" }
        return createWebClient(history, "history-service")
    }

    private fun createWebClient(baseUrl: String, name: String): WebClient {

        val insecureSslContext =
            SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()

        val provider = ConnectionProvider.builder(name)
            .maxConnections(10)
            .pendingAcquireMaxCount(-1) // default 20
            .pendingAcquireTimeout(Duration.ofSeconds(10))
            .build()

        val connector = ReactorClientHttpConnector(
            HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(10))
                .secure { it.sslContext(insecureSslContext) }
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
        )

        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(connector)
            .build()
    }
}
