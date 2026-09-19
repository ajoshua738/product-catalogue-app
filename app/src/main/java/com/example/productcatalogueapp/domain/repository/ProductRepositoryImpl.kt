package com.example.productcatalogueapp.data.repository

import com.example.productcatalogueapp.core.AppError
import com.example.productcatalogueapp.core.AppResult
import com.example.productcatalogueapp.core.Logger
import com.example.productcatalogueapp.data.mapper.toDomain
import com.example.productcatalogueapp.data.mapper.toDomainOrNull
import com.example.productcatalogueapp.data.remote.ProductAPI
import com.example.productcatalogueapp.domain.model.Product
import com.example.productcatalogueapp.domain.model.ProductPage
import com.example.productcatalogueapp.domain.repository.ProductRepository
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class ProductRepositoryImpl(
    private val api: ProductAPI,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): AppResult<ProductPage> =
        safeApiCall { api.getProducts(limit, skip).toDomain() }

    override suspend fun getProductDetail(id: Int): AppResult<Product> =
        when (val result = safeApiCall { api.getProduct(id) }) {
            is AppResult.Success ->
                result.data.toDomainOrNull()
                    ?.let { AppResult.Success(it) }
                    ?: AppResult.Failure(AppError.Serialization)

            is AppResult.Failure -> result
        }

    private suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> =
        withContext(ioDispatcher) {
            try {
                AppResult.Success(block())
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Logger.e(TAG, "Network failure", e)
                AppResult.Failure(AppError.Network)
            } catch (e: HttpException) {
                Logger.e(TAG, "HTTP ${e.code()}", e)
                AppResult.Failure(AppError.Http(e.code()))
            } catch (e: JsonDataException) {
                Logger.e(TAG, "Malformed response", e)
                AppResult.Failure(AppError.Serialization)
            } catch (e: Exception) {
                Logger.e(TAG, "Unexpected failure", e)
                AppResult.Failure(AppError.Unknown(e))
            }
        }

    private companion object {
        const val TAG = "ProductRepository"
    }
}
