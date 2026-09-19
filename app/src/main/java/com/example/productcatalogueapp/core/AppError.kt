package com.example.productcatalogueapp.core

// To handle failures
sealed class AppError {
    data object Network : AppError()
    data class Http(val code: Int) : AppError()
    data object Serialization : AppError()
    data class Unknown(val cause: Throwable) : AppError()
}
