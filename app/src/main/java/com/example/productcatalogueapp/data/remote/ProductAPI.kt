package com.example.productcatalogueapp.data.remote

import com.example.productcatalogueapp.data.remote.dto.ProductDTO
import com.example.productcatalogueapp.data.remote.dto.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductAPI {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductListResponse

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ProductDTO

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductListResponse
}