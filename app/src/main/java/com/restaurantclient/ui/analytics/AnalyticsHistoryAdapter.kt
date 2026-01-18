package com.restaurantclient.ui.analytics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.databinding.ItemAnalyticsHistoryBinding
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.util.DailyAnalyticsItem
import java.text.NumberFormat
import java.util.Locale

class AnalyticsHistoryAdapter : ListAdapter<DailyAnalyticsItem, AnalyticsHistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnalyticsHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAnalyticsHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyAnalyticsItem) {
            binding.blurView.setupGlassEffect(16f)

            binding.dateText.text = item.date
            binding.revenueText.text = NumberFormat.getCurrencyInstance(Locale.US).format(item.revenue)
            binding.orderCountText.text = "${item.orderCount} Orders"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DailyAnalyticsItem>() {
        override fun areItemsTheSame(oldItem: DailyAnalyticsItem, newItem: DailyAnalyticsItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyAnalyticsItem, newItem: DailyAnalyticsItem): Boolean {
            return oldItem == newItem
        }
    }
}
