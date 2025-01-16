package com.study.payment.infrastructure.config.webClient

import io.netty.channel.ChannelOption
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.cloud.client.loadbalancer.LoadBalanced
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
class WebClientConfig {

    fun createWebClient(baseUrl: String, name: String): WebClient {

        val insecureSslContext =
            SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()

        val provider = ConnectionProvider.builder(name)
            .maxConnections(10)
            .pendingAcquireTimeout(Duration.ofSeconds(10))
            .build()

        val connector = ReactorClientHttpConnector(
            HttpClient.create(provider)
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