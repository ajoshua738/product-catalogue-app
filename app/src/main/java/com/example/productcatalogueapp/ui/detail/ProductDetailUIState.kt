package com.example.productcatalogueapp.ui.detail

import com.example.productcatalogueapp.core.AppError
import com.example.productcatalogueapp.domain.model.Product

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Error(val error: AppError) : ProductDetailUiState()
}
