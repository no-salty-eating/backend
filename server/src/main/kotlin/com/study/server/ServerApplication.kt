package com.study.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
	runApplication<ServerApplication>(*args)
}
