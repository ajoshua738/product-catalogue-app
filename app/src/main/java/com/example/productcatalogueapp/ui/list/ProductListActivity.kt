package com.example.productcatalogueapp.ui.list

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productcatalogue.ui.list.LoadMoreAdapter
import com.example.productcatalogueapp.databinding.ActivityProductListBinding
import com.example.productcatalogueapp.di.ServiceLocator
import com.example.productcatalogueapp.ui.toMessageRes

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding

    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModelFactory(ServiceLocator.productRepository)
    }

    private val productAdapter = ProductAdapter()
    private val loadMoreAdapter = LoadMoreAdapter { viewModel.onRetryAppend() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObserver()

        binding.buttonRetry.setOnClickListener { viewModel.onRetry() }
    }

    private fun setupRecyclerView() = with(binding.recyclerProducts) {
        layoutManager = LinearLayoutManager(this@ProductListActivity)
        adapter = ConcatAdapter(productAdapter, loadMoreAdapter)
        setHasFixedSize(true)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                // The ViewModel re-checks its own guards, so a duplicate call is harmless.
                if (lastVisible >= layoutManager.itemCount - PREFETCH_DISTANCE) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupObserver() {
        viewModel.uiState.observe(this) { state ->
            renderScreenState(state)
            renderList(state)
        }
    }

    private fun renderScreenState(state: ProductListUiState) = with(binding) {
        progressInitial.visibility =
            if (state.screenState == ScreenState.Loading) View.VISIBLE else View.GONE

        recyclerProducts.visibility =
            if (state.screenState == ScreenState.Success) View.VISIBLE else View.GONE

        groupError.visibility =
            if (state.screenState == ScreenState.Error) View.VISIBLE else View.GONE

        groupEmpty.visibility =
            if (state.screenState == ScreenState.Empty) View.VISIBLE else View.GONE

        state.error?.let { textErrorBody.setText(it.toMessageRes()) }
    }

    private fun renderList(state: ProductListUiState) {
        productAdapter.submitList(state.products)
        loadMoreAdapter.state = when {
            state.appendFailed -> LoadMoreAdapter.State.Failed
            state.isAppending -> LoadMoreAdapter.State.Loading
            else -> LoadMoreAdapter.State.Hidden
        }
    }

    private companion object {
        const val PREFETCH_DISTANCE = 4
    }
}
