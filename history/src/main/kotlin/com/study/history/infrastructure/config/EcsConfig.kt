package com.study.history.infrastructure.config

import org.springframework.cloud.commons.util.InetUtils
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Configuration
class EcsConfig {

    companion object {
        private const val API_URL = "https://api.ipify.org"
        private val logger = LoggerProvider.logger
    }

    @Bean
    fun eurekaInstanceConfig(inetUtils: InetUtils): EurekaInstanceConfigBean {
        val eurekaInstanceConfigBean = EurekaInstanceConfigBean(inetUtils)

        val publicIp = try {
            val url = URL(API_URL)
            BufferedReader(InputStreamReader(url.openStream())).readLine()
        } catch (e: Exception) {
            logger.error { "Failed : ${e.message}" }
            ""
        }

        eurekaInstanceConfigBean.ipAddress = publicIp

        return eurekaInstanceConfigBean
    }

}