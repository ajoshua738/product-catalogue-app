package com.example.productcatalogueapp.ui.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.productcatalogueapp.databinding.ActivityProductListBinding
import com.example.productcatalogueapp.domain.model.Product

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val productAdapter = ProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = productAdapter
        productAdapter.submitList(sampleProducts())
    }


    // Test data to test the ui
    private fun sampleProducts() = listOf(
        Product(1, "Essence Mascara Lash Princess", "", 9.99, 2.56, "", emptyList(), "Essence", "beauty", 99),
        Product(2, "Eyeshadow Palette with Mirror", "", 19.99, 2.86, "", emptyList(), "Glamour", "beauty", 34),
        Product(3, "Powder Canister", "", 14.99, 4.64, "", emptyList(), "Velvet Touch", "beauty", 89)
    )
}
