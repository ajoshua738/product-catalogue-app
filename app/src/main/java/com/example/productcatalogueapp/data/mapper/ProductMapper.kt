package com.example.productcatalogueapp.data.mapper

import com.example.productcatalogueapp.data.remote.dto.ProductDTO
import com.example.productcatalogueapp.data.remote.dto.ProductListResponse
import com.example.productcatalogueapp.domain.model.Product
import com.example.productcatalogueapp.domain.model.ProductPage

fun ProductDTO.toDomainOrNull(): Product? {
    val safeId = id ?: return null
    return Product(
        id = safeId,
        title = title.orEmpty(),
        description = description.orEmpty(),
        price = price ?: 0.0,
        rating = rating ?: 0.0,
        thumbnailUrl = thumbnail.orEmpty(),
        imageUrls = images.orEmpty(),
        brand = brand.orEmpty(),
        category = category.orEmpty(),
        stock = stock ?: 0
    )
}


fun ProductListResponse.toDomain(): ProductPage = ProductPage(
    products = products.orEmpty().mapNotNull { it.toDomainOrNull() },
    total = total ?: 0
)
