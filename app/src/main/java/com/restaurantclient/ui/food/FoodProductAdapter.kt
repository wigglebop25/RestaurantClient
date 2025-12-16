package com.restaurantclient.ui.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ItemProductFoodBinding
import kotlin.random.Random

class FoodProductAdapter(
    private val onClick: (ProductResponse) -> Unit,
    private val onFavoriteClick: (ProductResponse) -> Unit
) : ListAdapter<ProductResponse, FoodProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private val favoriteStates = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductResponse) {
            binding.productName.text = product.name
            binding.productSubtitle.text = "Wendy's Burger" // Default subtitle
            
            // Generate a random rating for demo purposes
            val rating = String.format("%.1f", Random.nextDouble(3.5, 5.0))
            binding.ratingText.text = rating

            // Handle favorite state
            val isFavorite = favoriteStates[product.product_id] ?: false
            updateFavoriteIcon(isFavorite)

            binding.root.setOnClickListener {
                onClick(product)
            }

            binding.favoriteIcon.setOnClickListener {
                val newState = !(favoriteStates[product.product_id] ?: false)
                favoriteStates[product.product_id] = newState
                updateFavoriteIcon(newState)
                onFavoriteClick(product)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
        override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem.product_id == newItem.product_id
        }

        override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }
    }
}
