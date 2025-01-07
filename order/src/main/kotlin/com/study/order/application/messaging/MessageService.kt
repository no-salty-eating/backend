package com.study.order.application.messaging

interface MessageService {

    suspend fun send(topic: String, message: String)

    suspend fun <T> sendEvent(topic:String, event:T)
}