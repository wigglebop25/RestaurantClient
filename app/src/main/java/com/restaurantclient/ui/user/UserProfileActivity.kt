package com.restaurantclient.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.databinding.ActivityUserProfileBinding
import com.restaurantclient.ui.admin.AdminDashboardActivity
import com.restaurantclient.ui.admin.OrderManagementActivity
import com.restaurantclient.ui.admin.UserManagementActivity
import com.restaurantclient.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUserInfo()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        authViewModel.loadStoredUserInfo()
        
        val currentUser = authViewModel.getCurrentUser()
        val userRole = authViewModel.getUserRole()

        binding.usernameText.text = currentUser?.username ?: getString(R.string.profile_unknown_username)
        binding.roleChip.text = when (userRole) {
            RoleDTO.Admin -> getString(R.string.role_admin_label)
            RoleDTO.Customer -> getString(R.string.role_customer_label)
            else -> getString(R.string.profile_unknown_role)
        }
        binding.roleChip.isVisible = userRole != null
        binding.createdAtText.text = currentUser?.createdAt ?: getString(R.string.label_unknown_date)

        if (authViewModel.isAdmin()) {
            setupAdminShortcuts()
        } else {
            setupCustomerShortcuts()
        }
    }

    private fun setupAdminShortcuts() {
        binding.adminActionsCard.isVisible = true
        binding.customerActionsCard.isVisible = false

        binding.adminDashboardButton.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }
        binding.adminUsersButton.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }
        binding.adminOrdersButton.setOnClickListener {
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }
    }

    private fun setupCustomerShortcuts() {
        binding.adminActionsCard.isVisible = false
        binding.customerActionsCard.isVisible = true

        binding.customerOrdersButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.order.MyOrdersActivity::class.java))
        }
        binding.customerCartButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.cart.ShoppingCartActivity::class.java))
        }
        binding.customerCheckoutButton.setOnClickListener {
            startActivity(Intent(this, com.restaurantclient.ui.checkout.CheckoutActivity::class.java))
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Add admin dashboard button if user is admin
        if (authViewModel.isAdmin()) {
            // You can add a button to go back to dashboard here if needed
        }
    }
}
