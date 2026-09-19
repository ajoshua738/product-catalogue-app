package com.example.productcatalogueapp.ui.list

import com.example.productcatalogueapp.core.AppError
import com.example.productcatalogueapp.domain.model.Product

enum class ScreenState { Loading, Success, Error, Empty }

data class ProductListUiState(
    val screenState: ScreenState = ScreenState.Loading,
    val products: List<Product> = emptyList(),
    val error: AppError? = null,
    val total: Int = 0,
    val isAppending: Boolean = false,
    val appendFailed: Boolean = false,
    val endReached: Boolean = false
)
