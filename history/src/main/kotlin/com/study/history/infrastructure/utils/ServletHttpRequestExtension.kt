package com.study.history.infrastructure.utils

import org.springframework.http.server.reactive.ServerHttpRequest

private val mapReqIdToTxid = HashMap<String, String>()

var ServerHttpRequest.txid: String?
    get() {
        return mapReqIdToTxid[this.id]
    }
    set(value) {
        if (value == null) {
            mapReqIdToTxid.remove(id)
        } else {
            mapReqIdToTxid[id] = value
        }
    }
