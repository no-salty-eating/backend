package com.study.order.application.exception

open class RedisException(val error: Error) : RuntimeException()

class AcquireLockTimeoutException : RedisException(Error.ACQUIRE_LOCK_TIMEOUT)

class InternalServerError : RedisException(Error.INTERNAL_SERVER_ERROR)

class TimeoutCancellationError : RedisException(Error.TIMEOUT_CANCELLATION)