package com.example.productcatalogueapp.domain.repository

import com.example.productcatalogueapp.core.AppResult
import com.example.productcatalogueapp.domain.model.Product
import com.example.productcatalogueapp.domain.model.ProductPage

interface ProductRepository {
    suspend fun getProducts(limit: Int, skip: Int): AppResult<ProductPage>

    suspend fun getProductDetail(id: Int): AppResult<Product>

    suspend fun searchProducts(query: String, limit: Int, skip: Int): AppResult<ProductPage>
}
