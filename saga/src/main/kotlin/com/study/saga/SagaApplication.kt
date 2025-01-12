package com.study.saga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class SagaApplication

fun main(args: Array<String>) {
    runApplication<SagaApplication>(*args)
}
