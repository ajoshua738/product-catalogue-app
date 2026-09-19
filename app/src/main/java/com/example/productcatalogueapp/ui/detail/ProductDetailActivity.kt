package com.example.productcatalogueapp.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.productcatalogueapp.R
import com.example.productcatalogueapp.core.asPrice
import com.example.productcatalogueapp.core.asRating
import com.example.productcatalogueapp.databinding.ActivityProductDetailBinding
import com.example.productcatalogueapp.di.ServiceLocator
import com.example.productcatalogueapp.domain.model.Product
import com.google.android.material.tabs.TabLayoutMediator

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private val productId: Int by lazy { intent.getIntExtra(EXTRA_PRODUCT_ID, INVALID_ID) }

    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(ServiceLocator.productRepository, productId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (productId == INVALID_ID) {
            finish()
            return
        }

        binding.buttonBack.setOnClickListener { finish() }

        viewModel.uiState.observe(this) { state -> render(state) }
    }

    private fun render(state: ProductDetailUiState) = with(binding) {
        progressDetail.visibility =
            if (state is ProductDetailUiState.Loading) View.VISIBLE else View.GONE

        scrollContent.visibility =
            if (state is ProductDetailUiState.Success) View.VISIBLE else View.GONE

        when (state) {
            is ProductDetailUiState.Success -> bindProduct(state.product)

            is ProductDetailUiState.Error -> Unit
            ProductDetailUiState.Loading -> Unit
        }
    }

    private fun bindProduct(product: Product) = with(binding) {
        textTitle.text = product.title
        textPrice.text = product.price.asPrice()
        textRating.text = product.rating.asRating()
        textBrand.text = product.brand
        textStock.text = getString(R.string.detail_in_stock, product.stock)
        textDescription.text = product.description

        textBrand.visibility = if (product.brand.isBlank()) View.GONE else View.VISIBLE

        pagerImages.adapter = ProductImageAdapter(product.imageUrls)

        tabImageDots.visibility = if (product.imageUrls.size > 1) View.VISIBLE else View.GONE
        TabLayoutMediator(tabImageDots, pagerImages) { _, _ -> }.attach()
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "extra_product_id"
        private const val INVALID_ID = -1

        fun newIntent(context: Context, productId: Int): Intent =
            Intent(context, ProductDetailActivity::class.java)
                .putExtra(EXTRA_PRODUCT_ID, productId)
    }
}
