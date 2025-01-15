package com.study.order.infrastructure.config.redis

import org.redisson.Redisson
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RedissonConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: String,
) {

    @Bean
    fun redissonClient() : RedissonReactiveClient {
        val config = Config().apply {
            useSingleServer().apply {
                address = "redis://$host:$port"
            }
        }
        return Redisson.create(config).reactive()
    }

}