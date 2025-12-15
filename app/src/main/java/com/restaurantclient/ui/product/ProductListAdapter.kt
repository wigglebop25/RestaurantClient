package com.restaurantclient.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ItemProductBinding

class ProductListAdapter(
    private val onClick: (ProductResponse) -> Unit,
    private val onAdminAction: ((View, ProductResponse) -> Unit)? = null,
    private val isAdminMode: Boolean = false
) : ListAdapter<ProductResponse, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onClick: (ProductResponse) -> Unit,
        private val onAdminAction: ((View, ProductResponse) -> Unit)?,
        private val isAdminMode: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentProduct: ProductResponse? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let { onClick(it) }
            }
        }

        fun bind(product: ProductResponse) {
            currentProduct = product
            binding.productName.text = product.name
            binding.productDescription.text = product.description
            binding.productPrice.text = "$${product.price}"

            binding.adminBadgeChip.isVisible = isAdminMode
            binding.adminManageButton.isVisible = isAdminMode
            if (isAdminMode) {
                binding.adminManageButton.setOnClickListener { view ->
                    currentProduct?.let { onAdminAction?.invoke(view, it) }
                }
            } else {
                binding.adminManageButton.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onClick, onAdminAction, isAdminMode)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ProductDiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
    override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
        return oldItem.product_id == newItem.product_id
    }

    override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
        return oldItem == newItem
    }
}
