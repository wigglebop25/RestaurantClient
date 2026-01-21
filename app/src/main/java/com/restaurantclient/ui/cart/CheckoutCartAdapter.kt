package com.restaurantclient.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
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

    class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            val context = binding.root.context
            binding.apply {
                productName.text = cartItem.product.name
                productPrice.text = context.getString(R.string.cart_item_price_format, cartItem.product.price, cartItem.quantity)
                quantityText.text = cartItem.quantity.toString()
                
                val imageResource = ImageMapper.getDrawableResourceOrPlaceholder(cartItem.product.product_image_uri)
                productImage.setImageResource(imageResource)
                
                removeButton.visibility = View.GONE
                decrementButton.visibility = View.GONE
                incrementButton.visibility = View.GONE
                
                val itemTotal = cartItem.product.price.toDouble() * cartItem.quantity
                val formattedTotal = String.format(java.util.Locale.US, "%.2f", itemTotal)
                productPrice.text = context.getString(
                    R.string.checkout_item_total_format, 
                    formattedTotal, 
                    cartItem.quantity, 
                    cartItem.product.price
                )
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
