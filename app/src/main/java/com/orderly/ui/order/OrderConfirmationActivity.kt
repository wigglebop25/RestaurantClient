package com.orderly.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orderly.databinding.ActivityOrderConfirmationBinding
import com.orderly.ui.product.ProductListActivity

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getIntExtra(EXTRA_ORDER_ID, -1)
        val orderTotal = intent.getStringExtra(EXTRA_ORDER_TOTAL) ?: "$0.00"

        setupUI(orderId, orderTotal)
        setupClickListeners()
    }

    private fun setupUI(orderId: Int, orderTotal: String) {
        if (orderId > 0) {
            binding.orderIdText.text = "Order #$orderId"
        } else {
            binding.orderIdText.text = "Order Placed Successfully"
        }
        binding.orderTotalText.text = orderTotal
    }

    private fun setupClickListeners() {
        binding.viewOrdersButton.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
            finish()
        }

        binding.continueshoppingButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
        const val EXTRA_ORDER_TOTAL = "extra_order_total"
    }
}