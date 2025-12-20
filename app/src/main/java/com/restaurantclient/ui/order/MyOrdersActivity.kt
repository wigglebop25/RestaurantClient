package com.restaurantclient.ui.order

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.TokenManager
import com.restaurantclient.databinding.ActivityMyOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrdersBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter
    
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.root.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.action_my_orders)
            subtitle = getString(R.string.orders_screen_subtitle)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener { finish() }

        val refreshIcon = binding.root.findViewById<ImageView>(R.id.refresh_icon)
        refreshIcon.visibility = View.VISIBLE
        refreshIcon.setOnClickListener {
            val username = tokenManager.getUsername()
            if (username != null) {
                orderViewModel.fetchUserOrders(username)
                Toast.makeText(this, "Refreshing orders...", Toast.LENGTH_SHORT).show()
            }
        }

        setupGlassUI()
        
        orderListAdapter = OrderListAdapter { order ->
            startActivity(OrderDetailActivity.createIntent(this, order))
        }
        binding.ordersRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = orderListAdapter

        setupObservers()
        
        // Get username from token manager
        val username = tokenManager.getUsername()
        
        if (username != null) {
            orderViewModel.fetchUserOrders(username)
        } else {
            Log.e("MyOrdersActivity", "No username found, user not logged in properly")
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            
            // Redirect to MainActivity which will handle login
            val intent = android.content.Intent(this, com.restaurantclient.MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupGlassUI() {
        // Simplified - using MaterialCardView instead of BlurView for now
        binding.ordersSummaryGlass.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        binding.ordersSummaryGlass.clipToOutline = true
    }

    private fun setupObservers() {
        orderViewModel.userOrders.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Create a new list to ensure DiffUtil triggers
                    orderListAdapter.submitList(result.data.toList())
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to fetch orders: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
