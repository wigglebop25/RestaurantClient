package com.restaurantclient.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.databinding.ItemOrderBinding
import com.restaurantclient.util.DateTimeUtils
import java.math.BigDecimal
import java.text.NumberFormat

class OrderListAdapter(private val onClick: (OrderResponse) -> Unit) :
    ListAdapter<OrderResponse, OrderListAdapter.OrderViewHolder>(OrderDiffCallback) {

    class OrderViewHolder(private val binding: ItemOrderBinding, val onClick: (OrderResponse) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentOrder: OrderResponse? = null

        init {
            itemView.setOnClickListener {
                currentOrder?.let {
                    onClick(it)
                }
            }
        }

        fun bind(order: OrderResponse) {
            currentOrder = order
            binding.orderId.text = "Order #${order.order_id}"
            binding.orderStatus.text = order.status ?: "Pending"
            binding.orderDate.text = DateTimeUtils.formatIsoDate(order.created_at)
            binding.orderItemCount.text = "${order.quantity} items"
            binding.orderTotal.text = formatTotal(order.total_amount)
        }

        private fun formatTotal(total: String): String {
            return runCatching {
                val amount = BigDecimal(total)
                NumberFormat.getCurrencyInstance().format(amount)
            }.getOrElse {
                "\$${total}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        Log.d("OrderListAdapter", "Binding order at position $position: Order #${order.order_id}")
        holder.bind(order)
    }
    
    override fun submitList(list: List<OrderResponse>?) {
        Log.d("OrderListAdapter", "submitList called with ${list?.size ?: 0} items")
        super.submitList(list?.toList()) // Create new list instance
    }
}

object OrderDiffCallback : DiffUtil.ItemCallback<OrderResponse>() {
    override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem.order_id == newItem.order_id
    }

    override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem == newItem
    }
}
