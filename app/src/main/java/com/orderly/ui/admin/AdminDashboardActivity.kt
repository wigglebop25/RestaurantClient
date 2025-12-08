package com.orderly.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.MainActivity
import com.orderly.R
import com.orderly.databinding.ActivityAdminDashboardBinding
import com.orderly.ui.auth.AuthViewModel
import com.orderly.ui.order.MyOrdersActivity
import com.orderly.ui.product.ProductListActivity
import com.orderly.ui.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val adminViewModel: AdminDashboardViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        setupObservers()
        
        // Load dashboard data
        adminViewModel.loadDashboardData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Admin Dashboard"
    }

    private fun setupClickListeners() {
        // User Management Card
        binding.userManagementCard.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        // Product Management Card
        binding.productManagementCard.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        // Order Management Card
        binding.orderManagementCard.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        // Quick Actions
        binding.addUserButton.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }

        binding.viewReportsButton.setOnClickListener {
            // TODO: Implement reports functionality
            // startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    private fun setupObservers() {
        adminViewModel.dashboardStats.observe(this) { stats ->
            binding.totalUsersText.text = stats.totalUsers.toString()
            binding.totalOrdersText.text = stats.totalOrders.toString()
            binding.totalProductsText.text = stats.totalProducts.toString()
            binding.newUsersText.text = stats.newUsersToday.toString()
        }

        adminViewModel.recentUsers.observe(this) { users ->
            // Update recent users list
            // TODO: Implement RecyclerView for recent users
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.action_user_management -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
                true
            }
            R.id.action_product_list -> {
                startActivity(Intent(this, ProductListActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        authViewModel.logout()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh dashboard data when returning to screen
        adminViewModel.loadDashboardData()
    }
}