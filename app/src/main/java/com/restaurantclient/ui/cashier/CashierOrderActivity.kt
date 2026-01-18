package com.restaurantclient.ui.cashier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityCashierOrdersBinding
import com.restaurantclient.ui.cashier.CashierOrderAdapter
import com.restaurantclient.ui.cashier.CashierOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CashierOrderActivity : BaseCashierActivity() {

    private lateinit var binding: ActivityCashierOrdersBinding
    private val viewModel: CashierOrderViewModel by viewModels()
    private lateinit var adapter: CashierOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashierOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupObservers()
        setupRefresh()

        viewModel.loadOrders()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startPollingOrders()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPollingOrders()
    }

    private fun setupToolbar() {
        setupCashierToolbar(
            binding.toolbar.toolbar,
            getString(R.string.cashier_action_queue),
            showBackButton = true
        )
        binding.toolbar.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = CashierOrderAdapter { order, status ->
            viewModel.updateOrderStatus(order.order_id, status.uppercase())
        }
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = adapter
    }

    private fun setupFilters() {
        val filterOptions = listOf(
            "All Orders",
            "Pending",
            "Accepted",
            "Cooking",
            "Ready",
            "Completed",
            "Cancelled"
        )
        val adapter = android.widget.ArrayAdapter(this, R.layout.item_dropdown_menu, filterOptions)
        (binding.filterDropdown as? android.widget.AutoCompleteTextView)?.setAdapter(adapter)
        
        // Default selection
        binding.filterDropdown.setText(filterOptions[0], false)

        binding.filterDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedFilter = filterOptions[position]
            val filterKey = if (selectedFilter == "All Orders") "ALL" else selectedFilter
            viewModel.setFilter(filterKey)
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOrders(forceRefresh = true)
        }
    }

    private fun setupObservers() {
        viewModel.orders.observe(this) { result ->
            binding.swipeRefresh.isRefreshing = false
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                    updateEmptyState(result.data.isEmpty())
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    // Only show error on empty state to avoid toast spam during auto-refresh
                    if (adapter.currentList.isEmpty()) {
                        updateEmptyState(true)
                        val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                        Toast.makeText(this, "Failed to load orders: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.updateResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.order_update_success), Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    val finalMessage = if (result.exception.message?.contains("404") == true) {
                        "Order not found"
                    } else {
                        getString(R.string.order_update_error, message)
                    }
                    Toast.makeText(this, finalMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            // Only show main progress bar if list is empty
            if (adapter.currentList.isEmpty()) {
                binding.progressBar.isVisible = isLoading
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.isVisible = isEmpty
        binding.ordersRecyclerView.isVisible = !isEmpty
    }
}
