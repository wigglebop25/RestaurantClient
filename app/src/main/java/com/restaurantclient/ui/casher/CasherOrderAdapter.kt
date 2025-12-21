package com.restaurantclient.ui.casher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.databinding.ItemCasherOrderBinding
import com.restaurantclient.ui.common.setupGlassEffect

/**
 * Adapter for Casher Order List.
 * Uses ItemCasherOrderBinding (reusing layout) but logic is specific to Casher workflow.
 */
class CasherOrderAdapter(
    private val onStatusChange: (OrderResponse, String) -> Unit
) : ListAdapter<CasherOrderUIModel, CasherOrderAdapter.OrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemCasherOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemCasherOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val statusMap = mapOf(
            binding.chipPending.id to "Pending",
            binding.chipAccepted.id to "Accepted",
            binding.chipReady.id to "Ready",
            binding.chipCompleted.id to "Completed",
            binding.chipCancelled.id to "Cancelled"
        )

        init {
            setupBlur()
        }

        private fun setupBlur() {
            val context = binding.root.context
            val whiteOverlay = ContextCompat.getColor(context, R.color.white_glass_overlay)
            binding.orderCardBlur.setOverlayColor(whiteOverlay)
            binding.orderCardBlur.setupGlassEffect(20f)
        }

        fun bind(uiModel: CasherOrderUIModel) {
            val order = uiModel.order
            binding.orderIdText.text = "Order #${order.order_id}"
            
            val amount = order.total_amount.toDoubleOrNull() ?: 0.0
            binding.orderAmountText.text = java.text.NumberFormat.getCurrencyInstance().format(amount)
            
            binding.orderMetaText.text = buildMetaText(uiModel)
            binding.currentStatusLabel.text = order.status?.uppercase() ?: "PENDING"

            // Update Chip State
            binding.statusChipGroup.setOnCheckedStateChangeListener(null)
            val normalizedStatus = order.status?.lowercase() ?: "pending"
            
            statusMap.forEach { (chipId, statusValue) ->
                val chip = binding.statusChipGroup.findViewById<com.google.android.material.chip.Chip>(chipId)
                chip.isChecked = statusValue.lowercase() == normalizedStatus
            }

            binding.statusChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                val selectedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
                val newStatus = statusMap[selectedId] ?: return@setOnCheckedStateChangeListener
                
                // Avoid redundant updates
                if (!newStatus.equals(order.status, ignoreCase = true)) {
                    onStatusChange(order, newStatus)
                }
            }
        }

        private fun buildMetaText(uiModel: CasherOrderUIModel): String {
            val date = uiModel.order.created_at?.substringBefore("T") ?: ""
            return "${uiModel.username} • Placed $date"
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CasherOrderUIModel>() {
        override fun areItemsTheSame(oldItem: CasherOrderUIModel, newItem: CasherOrderUIModel): Boolean =
            oldItem.order.order_id == newItem.order.order_id

        override fun areContentsTheSame(oldItem: CasherOrderUIModel, newItem: CasherOrderUIModel): Boolean =
            oldItem == newItem
    }
}

