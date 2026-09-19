package com.example.productcatalogueapp.domain.model

// Product data class for the list page
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val thumbnailUrl: String,
    val imageUrls: List<String>,
    val brand: String,
    val category: String,
    val stock: Int
)