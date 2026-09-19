package com.example.productcatalogueapp.ui

import androidx.annotation.StringRes
import com.example.productcatalogueapp.R
import com.example.productcatalogueapp.core.AppError

@StringRes
fun AppError.toMessageRes(): Int = when (this) {
    AppError.Network -> R.string.error_network
    is AppError.Http -> R.string.error_server
    AppError.Serialization -> R.string.error_unexpected
    is AppError.Unknown -> R.string.error_unexpected
}