package com.restaurantclient.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.data.CartItem
import com.restaurantclient.databinding.ItemCartBinding
import com.restaurantclient.util.ImageMapper

class CheckoutCartAdapter : ListAdapter<CartItem, CheckoutCartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                productName.text = cartItem.product.name
                productPrice.text = "$${cartItem.product.price} x ${cartItem.quantity}"
                quantityText.text = cartItem.quantity.toString()
                
                // Load product image from local resources
                val imageResource = ImageMapper.getDrawableResourceOrPlaceholder(cartItem.product.product_image_uri)
                productImage.setImageResource(imageResource)
                
                // Hide controls in checkout mode
                removeButton.visibility = View.GONE
                decrementButton.visibility = View.GONE
                incrementButton.visibility = View.GONE
                
                // Show total for this item
                val itemTotal = cartItem.product.price.toDouble() * cartItem.quantity
                productPrice.text = "$${String.format("%.2f", itemTotal)} (${cartItem.quantity} x $${cartItem.product.price})"
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.product_id == newItem.product.product_id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
