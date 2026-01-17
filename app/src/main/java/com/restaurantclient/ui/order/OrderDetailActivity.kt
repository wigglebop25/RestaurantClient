package com.restaurantclient.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.OrderResponse
import com.restaurantclient.databinding.ActivityOrderDetailBinding
import com.restaurantclient.util.DateTimeUtils
import com.restaurantclient.util.ErrorUtils
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModels()
    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val initialOrder = intent.getSerializableExtra(EXTRA_ORDER, OrderResponse::class.java)
        if (initialOrder == null) {
            Toast.makeText(this, R.string.order_detail_error_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        orderId = initialOrder.order_id
        renderOrder(initialOrder)
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        if (orderId != -1) {
            viewModel.startPolling(orderId)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.customerToolbar.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.order_detail_title)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.customerToolbar.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObservers() {
        viewModel.order.observe(this) { result: Result<OrderResponse> ->
            when (result) {
                is Result.Success<OrderResponse> -> {
                    renderOrder(result.data)
                }
                is Result.Error -> {
                    // Don't show toast for every polling failure to avoid spam
                    // but log it or show a small indicator if needed
                    val message = ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    android.util.Log.e("OrderDetail", "Failed to update order status: $message")
                }
            }
        }
    }

    private fun renderOrder(order: OrderResponse) {
        binding.orderNumberValue.text = "#${order.order_id}"
        binding.orderStatusChip.text = order.status ?: getString(R.string.order_detail_unknown_value)

        val totalQuantity = order.products?.sumOf { it.quantity } ?: 0
        binding.orderQuantityValue.text = totalQuantity.toString()
        
        binding.orderTotalValue.text = NumberFormat.getCurrencyInstance().format(order.total_amount)
        binding.orderPlacedValue.text = DateTimeUtils.formatIsoDate(order.created_at)
        binding.orderUpdatedValue.text = DateTimeUtils.formatIsoDate(order.updated_at)
        
        // Update chip style based on status if needed
        updateStatusChipStyle(order.status)
    }

    private fun updateStatusChipStyle(status: String?) {
        val color = when (status?.uppercase()) {
            "PENDING" -> getColor(R.color.status_pending)
            "READY" -> getColor(R.color.status_ready)
            "COMPLETED" -> getColor(R.color.status_completed)
            "CANCELLED" -> getColor(R.color.status_cancelled)
            else -> getColor(R.color.status_pending)
        }
        binding.orderStatusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
    }

    private fun formatTimestamp(value: String?): String {
        return DateTimeUtils.formatIsoDate(value).ifBlank {
            getString(R.string.order_detail_unknown_value)
        }
    }

    companion object {
        private const val EXTRA_ORDER = "extra_order"

        fun createIntent(context: Context, order: OrderResponse): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(EXTRA_ORDER, order)
            }
        }
    }
}