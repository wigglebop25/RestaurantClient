package com.restaurantclient.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.data.dto.OrderProductResponse
import com.restaurantclient.databinding.ItemOrderDetailProductBinding
import com.restaurantclient.util.ImageMapper
import java.text.NumberFormat

class OrderDetailAdapter : ListAdapter<OrderProductResponse, OrderDetailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderDetailProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemOrderDetailProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderProductResponse) {
            val currencyFormat = NumberFormat.getCurrencyInstance()
            
            binding.productName.text = item.product?.name ?: "Unknown Product"
            // Use the string resource properly if you pass context or string, but for adapter simplicity we format here
            // Assuming quantity label is "Quantity"
            binding.productQuantity.text = "Qty: ${item.quantity}" 
            binding.productUnitPrice.text = "(${currencyFormat.format(item.unit_price)})"
            binding.productLineTotal.text = currencyFormat.format(item.line_total)

            val imageRes = ImageMapper.getDrawableResourceOrPlaceholder(item.product?.product_image_uri)
            binding.productImage.setImageResource(imageRes)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderProductResponse>() {
        override fun areItemsTheSame(oldItem: OrderProductResponse, newItem: OrderProductResponse): Boolean {
            return oldItem.product_id == newItem.product_id
        }

        override fun areContentsTheSame(oldItem: OrderProductResponse, newItem: OrderProductResponse): Boolean {
            return oldItem == newItem
        }
    }
}