package com.example.productcatalogue.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.productcatalogueapp.databinding.ItemLoadMoreBinding


// The single row footer, added after the product list
class LoadMoreAdapter(
    private val onRetry: () -> Unit
) : RecyclerView.Adapter<LoadMoreAdapter.LoadMoreViewHolder>() {

    enum class State { Hidden, Loading, Failed }

    var state: State = State.Hidden
        set(value) {
            if (field == value) return
            val wasVisible = field != State.Hidden
            field = value
            when {
                wasVisible && value == State.Hidden -> notifyItemRemoved(0)
                !wasVisible -> notifyItemInserted(0)
                else -> notifyItemChanged(0)
            }
        }

    override fun getItemCount() = if (state == State.Hidden) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadMoreViewHolder {
        val binding = ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadMoreViewHolder(binding, onRetry)
    }

    override fun onBindViewHolder(holder: LoadMoreViewHolder, position: Int) {
        holder.bind(state)
    }

    class LoadMoreViewHolder(
        private val binding: ItemLoadMoreBinding,
        private val onRetry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(state: State) = with(binding) {
            val failed = state == State.Failed
            progressLoadMore.visibility = if (failed) View.GONE else View.VISIBLE
            textLoadMore.visibility = if (failed) View.GONE else View.VISIBLE
            groupLoadMoreError.visibility = if (failed) View.VISIBLE else View.GONE
            buttonRetryAppend.setOnClickListener { onRetry() }
        }
    }
}
