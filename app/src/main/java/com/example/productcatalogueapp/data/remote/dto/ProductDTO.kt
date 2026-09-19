package com.example.productcatalogueapp.data.remote.dto


// DTO of JSON returned from API
data class ProductDTO(
    val id: Int?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val rating: Double?,
    val thumbnail: String?,
    val images: List<String>?,
    val brand: String?,
    val category: String?,
    val stock: Int?
)
