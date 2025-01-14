package com.study.order.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class RedisException(val error: Error) : RuntimeException()

@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
class AcquireLockTimeoutException : RedisException(Error.ACQUIRE_LOCK_TIMEOUT)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerError : RedisException(Error.INTERNAL_SERVER_ERROR)