package com.study.order.application.exception

open class ServerException(val error: Error) : RuntimeException()

class AcquireLockTimeoutException : ServerException(Error.ACQUIRE_LOCK_TIMEOUT)

class InternalServerError : ServerException(Error.INTERNAL_SERVER_ERROR)

class TimeoutCancellationError : ServerException(Error.TIMEOUT_CANCELLATION)