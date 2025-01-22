package com.study.order.infrastructure.config.r2dbc

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class DataSourceConfig(
    @Value("\${spring.r2dbc.url}")
    private val url: String,
    @Value("\${spring.r2dbc.username}")
    private val username: String,
    @Value("\${spring.r2dbc.password}")
    private val password: String
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .from(ConnectionFactoryOptions.parse(url))
                .option(ConnectionFactoryOptions.USER, username)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .build()
        )

        val poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
            .initialSize(50)
            .minIdle(25)
            .maxSize(100)
            .build()

        val pool = ConnectionPool(poolConfig)

        // Warmup the pool
        pool.warmup().subscribe()

        return pool
    }
}