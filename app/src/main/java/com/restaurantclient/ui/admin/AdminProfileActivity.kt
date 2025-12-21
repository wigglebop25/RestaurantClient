package com.restaurantclient.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.databinding.ActivityAdminProfileBinding
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.ui.user.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassUI()
        setupUserInfo()
        setupClickListeners()
        setupObservers()

        val currentUserId = authViewModel.getUserId()
        currentUserId?.let { userId ->
            userProfileViewModel.loadUserProfile(userId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.apply {
            title = "Admin Profile"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.adminToolbar.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupGlassUI() {
        binding.profileGlassCard.setupGlassEffect(25f)
        binding.profileGlassCard.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        binding.profileGlassCard.clipToOutline = true

        binding.adminActionsBlur.setupGlassEffect(20f)
        binding.adminActionsBlur.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND)
        binding.adminActionsBlur.clipToOutline = true
    }

    private fun setupUserInfo() {
        authViewModel.loadStoredUserInfo()
        val currentUser = authViewModel.getCurrentUser()
        binding.usernameText.text = currentUser?.username ?: "Admin"
    }

    private fun setupClickListeners() {
        binding.adminDashboardButton.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
        binding.adminUsersButton.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }
        binding.adminOrdersButton.setOnClickListener {
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }
        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { result ->
            if (result is Result.Success) {
                binding.usernameText.text = result.data.username
            }
        }
    }
}
