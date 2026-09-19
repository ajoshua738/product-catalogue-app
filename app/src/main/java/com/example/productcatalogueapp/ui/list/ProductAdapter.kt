package com.example.productcatalogueapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.productcatalogueapp.core.asPrice
import com.example.productcatalogueapp.core.asRating
import com.example.productcatalogueapp.databinding.ItemProductBinding
import com.example.productcatalogueapp.domain.model.Product
import coil.load
import com.example.productcatalogueapp.R

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) = with(binding) {
            textTitle.text = product.title
            textPrice.text = product.price.asPrice()
            textRating.text = product.rating.asRating()

            imageThumbnail.load(product.thumbnailUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
            }
        }

    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product) =
                oldItem == newItem
        }
    }
}
