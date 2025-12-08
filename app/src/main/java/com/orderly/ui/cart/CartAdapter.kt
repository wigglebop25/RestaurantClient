package com.orderly.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orderly.data.CartItem
import com.orderly.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

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
                productPrice.text = "$${cartItem.product.price}"
                quantityText.text = cartItem.quantity.toString()

                // Remove item
                removeButton.setOnClickListener {
                    onRemoveItem(cartItem.product.product_id)
                }

                // Decrease quantity
                decrementButton.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity > 0) {
                        onQuantityChanged(cartItem.product.product_id, newQuantity)
                    } else {
                        onRemoveItem(cartItem.product.product_id)
                    }
                }

                // Increase quantity
                incrementButton.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem.product.product_id, newQuantity)
                }
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