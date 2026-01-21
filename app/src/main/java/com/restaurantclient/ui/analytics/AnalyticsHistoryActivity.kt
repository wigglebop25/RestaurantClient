package com.restaurantclient.ui.analytics

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.restaurantclient.databinding.ActivityAnalyticsHistoryBinding
import com.restaurantclient.util.ToastManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyticsHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsHistoryBinding
    private val viewModel: AnalyticsHistoryViewModel by viewModels()
    private val adapter = AnalyticsHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupRecyclerView()
        observeViewModel()

        viewModel.loadHistory()
    }

    private fun setupRecyclerView() {
        binding.analyticsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.history.observe(this) { history ->
            adapter.submitList(history)
            binding.emptyText.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                ToastManager.showToast(this, errorMsg, android.widget.Toast.LENGTH_LONG)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
