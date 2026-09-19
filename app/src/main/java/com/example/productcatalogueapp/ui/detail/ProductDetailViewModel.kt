package com.example.productcatalogueapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.productcatalogueapp.core.AppResult
import com.example.productcatalogueapp.core.Logger
import com.example.productcatalogueapp.domain.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository,
    private val productId: Int
) : ViewModel() {

    private val _uiState = MutableLiveData<ProductDetailUiState>()
    val uiState: LiveData<ProductDetailUiState> = _uiState

    init {
        load()
    }

    fun onRetry() = load()

    private fun load() {
        _uiState.value = ProductDetailUiState.Loading

        viewModelScope.launch {
            _uiState.value = when (val result = repository.getProductDetail(productId)) {
                is AppResult.Success -> ProductDetailUiState.Success(result.data)
                is AppResult.Failure -> {
                    Logger.e(TAG, "Detail $productId failed: ${result.error}")
                    ProductDetailUiState.Error(result.error)
                }
            }
        }
    }

    private companion object {
        const val TAG = "ProductDetailViewModel"
    }
}

class ProductDetailViewModelFactory(
    private val repository: ProductRepository,
    private val productId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(repository, productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
