package com.example.productcatalogueapp.data.remote.dto

// Response returned by list and search API
data class ProductListResponse(
    val products: List<ProductDTO>?,
    val total: Int?,
    val skip: Int?,
    val limit: Int?
)