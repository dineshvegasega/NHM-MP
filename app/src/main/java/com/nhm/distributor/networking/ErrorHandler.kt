package com.nhm.distributor.networking


sealed class ErrorHandler<T> (
    var data: T? = null,
    var message: String? = null
) {

    class Success<T>(data: T) : ErrorHandler<T>(data)

    class Error<T>(message: String, data: T? = null) : ErrorHandler<T>(data, message)

    class Loading<T>() : ErrorHandler<T>()

}