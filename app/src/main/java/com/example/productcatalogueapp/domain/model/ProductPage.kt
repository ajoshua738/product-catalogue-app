package com.example.productcatalogueapp.domain.model

// Data class for the product list, for pagination
data class ProductPage(
    val products: List<Product>,
    val total: Int
)
