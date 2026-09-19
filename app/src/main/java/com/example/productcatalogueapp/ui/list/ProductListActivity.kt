package com.example.productcatalogueapp.ui.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productcatalogue.ui.list.LoadMoreAdapter
import com.example.productcatalogueapp.databinding.ActivityProductListBinding
import com.example.productcatalogueapp.di.ServiceLocator
import com.example.productcatalogueapp.ui.detail.ProductDetailActivity
import com.example.productcatalogueapp.ui.toMessageRes

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding

    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModelFactory(ServiceLocator.productRepository)
    }

    private val productAdapter = ProductAdapter { product ->
        startActivity(ProductDetailActivity.newIntent(this, product.id))
    }

    private val loadMoreAdapter = LoadMoreAdapter { viewModel.onRetryAppend() }

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchInput()
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

                if (lastVisible >= layoutManager.itemCount - PREFETCH_DISTANCE) {
                    viewModel.loadNextPage()
                }
            }
        })
    }


    private fun setupSearchInput() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty().trim()
                val runnable = Runnable { viewModel.onSearch(query) }
                searchRunnable = runnable
                searchHandler.postDelayed(runnable, SEARCH_DEBOUNCE_MS)
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

    override fun onDestroy() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    private companion object {
        const val PREFETCH_DISTANCE = 4
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
