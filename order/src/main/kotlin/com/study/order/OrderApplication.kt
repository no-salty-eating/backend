package com.study.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
