package com.restaurantclient.ui.admin

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.databinding.ActivityAdminDashboardBinding
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.ui.product.ProductListActivity
import com.restaurantclient.ui.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import android.graphics.Color

import com.restaurantclient.ui.analytics.AnalyticsHistoryActivity

@AndroidEntryPoint
class AdminDashboardActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val adminViewModel: AdminDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupToolbar()
        setupGlassEffects()
        setupClickListeners()
        setupObservers()
        
        // Load dashboard data
        adminViewModel.loadDashboardData()
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.admin_primary)
    }

    private fun setupToolbar() {
        setupAdminToolbar(binding.adminToolbar.toolbar, getString(R.string.admin_dashboard_title))
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
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }
        
        // Role Management Card
        binding.roleManagementCard.setOnClickListener {
            startActivity(Intent(this, RoleManagementActivity::class.java))
        }

        // Quick Actions
        binding.addUserButton.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }

        binding.viewReportsButton.setOnClickListener {
            showReportsPlaceholderDialog()
        }

        // View History
        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, AnalyticsHistoryActivity::class.java))
        }
    }

    private fun setupObservers() {
        adminViewModel.dashboardStats.observe(this) { stats ->
            binding.totalUsersText.text = stats.totalUsers.toString()
            binding.totalOrdersText.text = stats.totalOrders.toString()
            binding.totalProductsText.text = stats.totalProducts.toString()
            binding.totalRolesText.text = stats.totalRoles.toString()
            
            // Format currency
            val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
            binding.totalRevenueText.text = currencyFormat.format(stats.totalRevenue)
            binding.avgOrderText.text = currencyFormat.format(stats.averageOrderValue)
            
            // Update Bar Chart
            updateRevenueChart(stats.dailyRevenue)
        }
        
        adminViewModel.dashboardSummary.observe(this) { summary ->
            // Enhanced dashboard with order status breakdown
            binding.totalOrdersText.text = summary.orderCount.toString()
            binding.totalUsersText.text = summary.userCount.toString()
            binding.totalProductsText.text = summary.productCount.toString()
            // Can add more detailed stats if UI supports it
        }
    }

    private fun updateRevenueChart(dailyRevenue: Map<String, Double>) {
        android.util.Log.d("AdminDashboard", "Updating revenue chart with ${dailyRevenue.size} entries: $dailyRevenue")
        
        if (dailyRevenue.isEmpty()) {
            binding.revenueBarChart.setNoDataText("No revenue data available")
            binding.revenueBarChart.invalidate()
            return
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        
        // Sort dates to ensure chronological order (Keys are now yyyy-MM-dd, so string sort works)
        val sortedDates = dailyRevenue.keys.sorted()
        
        // Input: "2026-01-18", Output: "Jan 18"
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val outputFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.US)
        
        sortedDates.forEachIndexed { index, dateStr ->
            entries.add(BarEntry(index.toFloat(), dailyRevenue[dateStr]?.toFloat() ?: 0f))
            
            val label = try {
                val date = inputFormat.parse(dateStr)
                if (date != null) outputFormat.format(date) else dateStr
            } catch (e: Exception) {
                dateStr
            }
            labels.add(label)
        }

        val dataSet = BarDataSet(entries, "Daily Revenue")
        dataSet.color = ContextCompat.getColor(this, R.color.admin_primary)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.white)
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        binding.revenueBarChart.data = barData
        
        // Chart Styling
        binding.revenueBarChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setTouchEnabled(true)
            setPinchZoom(false) // Disable pinch zoom to prefer scrolling
            isDragEnabled = true // Enable scrolling
            setScaleEnabled(false) // Disable general scaling to keep bar width consistent
            animateY(1000)
            
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(this@AdminDashboardActivity, R.color.white)
                setDrawGridLines(false)
                granularity = 1f
                isGranularityEnabled = true
                labelCount = labels.size
            }
            
            axisLeft.apply {
                textColor = ContextCompat.getColor(this@AdminDashboardActivity, R.color.white)
                setDrawGridLines(true)
                gridColor = Color.argb(40, 255, 255, 255)
                axisMinimum = 0f
            }
            
            axisRight.isEnabled = false
            
            // Scalability: Show only 7 days at a time, scrollable
            setVisibleXRangeMaximum(7f)
            // Scroll to the end (latest date)
            moveViewToX(entries.size.toFloat())
        }
        
        binding.revenueBarChart.invalidate()
    }

    private fun bindRoleChip(chip: Chip, role: RoleDTO?) {
        val resolvedRole = role ?: RoleDTO.Customer
        chip.text = resolvedRole.name.uppercase()
        val colorRes = if (resolvedRole == RoleDTO.Admin) R.color.admin_primary else R.color.admin_secondary
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))
    }

    private fun formatDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) {
            return getString(R.string.label_unknown_date)
        }
        return rawDate.substringBefore("T")
    }

    private fun showReportsPlaceholderDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reports_dialog_title)
            .setMessage(R.string.reports_dialog_message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, AdminProfileActivity::class.java))
                true
            }
            R.id.action_user_management -> {
                startActivity(Intent(this, UserManagementActivity::class.java))
                true
            }
            R.id.action_role_management -> {
                startActivity(Intent(this, RoleManagementActivity::class.java))
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

    override fun onPause() {
        super.onPause()
    }

    private fun setupGlassEffects() {
        // Statistics cards
        binding.totalUsersBlur.setupGlassEffect(20f)
        binding.totalOrdersBlur.setupGlassEffect(20f)
        binding.totalProductsBlur.setupGlassEffect(20f)
        binding.totalRolesBlur.setupGlassEffect(20f)
        binding.totalRevenueBlur.setupGlassEffect(20f)
        binding.avgOrderBlur.setupGlassEffect(20f)
        binding.revenueChartBlur.setupGlassEffect(20f)
        
        // Management cards
        binding.userManagementBlur.setupGlassEffect(20f)
        binding.productManagementBlur.setupGlassEffect(20f)
        binding.orderManagementBlur.setupGlassEffect(20f)
        binding.roleManagementBlur.setupGlassEffect(20f)
    }
}
