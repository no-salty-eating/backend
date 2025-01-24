package com.study.order.infrastructure.config.r2dbc

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import org.mariadb.r2dbc.MariadbConnectionFactoryProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import reactor.netty.resources.LoopResources
import java.time.Duration

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

        println("HELLO : ${Runtime.getRuntime().availableProcessors()}")

        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .from(ConnectionFactoryOptions.parse(url))
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DRIVER, "pool")
                .option(PROTOCOL, "mariadb")
                .option(
                    MariadbConnectionFactoryProvider.LOOP_RESOURCES,
                    LoopResources.create("r2dbc-loop", -1, 10, true, false)
                )
                .build()
        )


        val poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
            .initialSize(10)
            .maxSize(10)
            .maxIdleTime(Duration.ofMinutes(30))
            .maxLifeTime(Duration.ofMinutes(-1))
            .maxAcquireTime(Duration.ofSeconds(-1))
            .maxCreateConnectionTime(Duration.ofSeconds(-1))
            .validationQuery("select 1")
            .build()

        val pool = ConnectionPool(poolConfig)

        pool.warmup().subscribe()

        return pool
    }
}