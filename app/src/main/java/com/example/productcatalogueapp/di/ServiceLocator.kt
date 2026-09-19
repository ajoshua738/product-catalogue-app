package com.example.productcatalogueapp.di

import com.example.productcatalogueapp.data.remote.NetworkModule
import com.example.productcatalogueapp.data.repository.ProductRepositoryImpl
import com.example.productcatalogueapp.domain.repository.ProductRepository

object ServiceLocator {

    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(NetworkModule.productApi)
    }
}
