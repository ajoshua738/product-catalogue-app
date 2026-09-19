package com.example.productcatalogueapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.productcatalogueapp.core.AppResult
import com.example.productcatalogueapp.core.Logger
import com.example.productcatalogueapp.domain.model.ProductPage
import com.example.productcatalogueapp.domain.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<ProductListUiState>()
    val uiState: LiveData<ProductListUiState> = _uiState

    private var state = ProductListUiState()
    private var loadJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onRetry() = loadFirstPage()

    fun onRetryAppend() = loadNextPage(force = true)

    fun loadNextPage(force: Boolean = false) {
        if (state.screenState != ScreenState.Success) return
        if (state.isAppending || state.endReached) return
        if (state.appendFailed && !force) return

        setState(state.copy(isAppending = true, appendFailed = false))

        viewModelScope.launch {
            when (val result = repository.getProducts(PAGE_SIZE, state.products.size)) {
                is AppResult.Success -> {
                    val combined = state.products + result.data.products
                    setState(
                        state.copy(
                            products = combined,
                            total = result.data.total,
                            isAppending = false,
                            endReached = isEndReached(combined.size, result.data)
                        )
                    )
                }

                is AppResult.Failure -> {
                    Logger.e(TAG, "Append failed: ${result.error}")
                    setState(state.copy(isAppending = false, appendFailed = true))
                }
            }
        }
    }

    private fun loadFirstPage() {
        loadJob?.cancel()

        setState(
            state.copy(
                screenState = ScreenState.Loading,
                isAppending = false,
                appendFailed = false,
                error = null
            )
        )

        loadJob = viewModelScope.launch {
            when (val result = repository.getProducts(PAGE_SIZE, 0)) {
                is AppResult.Success -> {
                    val page = result.data
                    setState(
                        state.copy(
                            screenState = if (page.products.isEmpty()) {
                                ScreenState.Empty
                            } else {
                                ScreenState.Success
                            },
                            products = page.products,
                            total = page.total,
                            endReached = isEndReached(page.products.size, page),
                            error = null
                        )
                    )
                }

                is AppResult.Failure -> {
                    Logger.e(TAG, "First page failed: ${result.error}")
                    setState(state.copy(screenState = ScreenState.Error, error = result.error))
                }
            }
        }
    }


    // End pagination on empty page
    private fun isEndReached(loaded: Int, page: ProductPage) =
        page.products.isEmpty() || loaded >= page.total

    private fun setState(newState: ProductListUiState) {
        state = newState
        _uiState.value = newState
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val TAG = "ProductListViewModel"
    }
}

class ProductListViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
