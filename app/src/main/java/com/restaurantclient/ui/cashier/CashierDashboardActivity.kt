package com.restaurantclient.ui.cashier

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityCashierDashboardBinding
import com.restaurantclient.ui.common.setupGlassEffect
import dagger.hilt.android.AndroidEntryPoint

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import android.graphics.Color

@AndroidEntryPoint
class CashierDashboardActivity : BaseCashierActivity() {

    private lateinit var binding: ActivityCashierDashboardBinding
    private val viewModel: CashierDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashierDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBlurEffects()
        setupClickListeners()
        setupObservers()

        viewModel.loadDashboardData()
    }

    private fun setupToolbar() {
        setupCashierToolbar(
            binding.toolbarContainer.toolbar,
            getString(R.string.cashier_dashboard_title),
            showBackButton = false
        )
    }

    private fun setupBlurEffects() {
        // Stats Cards
        binding.statPendingBlur.setupGlassEffect(15f)
        binding.statTotalOrdersBlur.setupGlassEffect(15f)
        binding.statCompletedBlur.setupGlassEffect(15f)
        binding.statRevenueBlur.setupGlassEffect(15f)
        binding.revenueChartBlur.setupGlassEffect(15f)
        
        // Buttons
        binding.btnOrdersBlur.setupGlassEffect(20f)
    }

    private fun setupClickListeners() {
        binding.btnManageOrders.setOnClickListener {
            // Navigate to Order Queue
            startActivity(Intent(this, CashierOrderActivity::class.java))
        }
    }

    private fun setupObservers() {
        // Initial welcome text from cached data
        val cachedUsername = authViewModel.getCurrentUser()?.username 
            ?: authViewModel.getUserRole()?.name 
            ?: "Cashier"
        binding.welcomeText.text = getString(R.string.cashier_welcome, cachedUsername)

        viewModel.dashboardStats.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val stats = result.data
                    binding.statPendingCount.text = stats.pendingOrders.toString()
                    binding.statTotalOrdersCount.text = stats.orderCount.toString()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    android.widget.Toast.makeText(this, "Failed to load stats: $message", android.widget.Toast.LENGTH_LONG).show()
                    binding.statPendingCount.text = "-"
                    binding.statTotalOrdersCount.text = "-"
                }
            }
        }
        
        viewModel.analytics.observe(this) { analytics ->
            binding.statCompletedCount.text = analytics.completedOrders.toString()
            val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
            binding.statRevenueCount.text = currencyFormat.format(analytics.totalRevenue)
            
            // Also update other stats to be consistent if they came from analytics
            binding.statPendingCount.text = analytics.pendingOrders.toString()
            binding.statTotalOrdersCount.text = analytics.totalOrders.toString()

            // Update chart
            updateRevenueChart(analytics.dailyRevenue)
        }

        viewModel.currentUser.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.welcomeText.text = getString(R.string.cashier_welcome, result.data.username)
                }
                is Result.Error -> {
                    // Fallback to stored username
                    authViewModel.loadStoredUserInfo()
                    val username = authViewModel.getCurrentUser()?.username ?: "Cashier"
                    binding.welcomeText.text = getString(R.string.cashier_welcome, username)
                    android.util.Log.e("CashierDashboard", "Failed to fetch user details: ${result.exception.message}")
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateRevenueChart(dailyRevenue: Map<String, Double>) {
        android.util.Log.d("CashierDashboard", "Updating revenue chart with ${dailyRevenue.size} entries: $dailyRevenue")
        
        if (dailyRevenue.isEmpty()) {
            binding.revenueBarChart.setNoDataText("No live data")
            return
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val sortedDates = dailyRevenue.keys.sorted().takeLast(7) // Show last 7 days

        sortedDates.forEachIndexed { index, date ->
            entries.add(BarEntry(index.toFloat(), dailyRevenue[date]?.toFloat() ?: 0f))
            labels.add(date.takeLast(5))
        }

        val dataSet = BarDataSet(entries, "Revenue")
        dataSet.color = ContextCompat.getColor(this, R.color.itadaki_crimson)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.itadaki_white)
        
        binding.revenueBarChart.data = BarData(dataSet)
        binding.revenueBarChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            animateY(800)
            
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
        }
        binding.revenueBarChart.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_main_menu, menu) // Reuse admin menu for logout for now
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
}