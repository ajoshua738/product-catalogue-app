package com.example.productcatalogueapp.data.remote.dto

import retrofit2.http.GET
import retrofit2.http.Query

interface ProductAPI {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductListResponse
}
