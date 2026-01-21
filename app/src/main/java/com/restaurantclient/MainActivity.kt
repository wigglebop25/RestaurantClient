package com.restaurantclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDTO
import com.restaurantclient.ui.admin.AdminDashboardActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.ui.auth.LoginActivity
import com.restaurantclient.ui.cashier.CashierDashboardActivity
import com.restaurantclient.ui.product.ProductListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            determineUserRoleAndNavigate()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Observe refresh result to navigate with FRESH role data from backend
        authViewModel.refreshResult.observe(this) { result ->
            if (result is Result.Success) {
                authViewModel.loadStoredUserInfo()
                navigateBasedOnRole()
            } else {
                Log.w("MainActivity", "Token refresh failed. Using cached role if available.")
                determineUserRoleAndNavigate() 
            }
        }

        if (authViewModel.isLoggedIn()) {
            authViewModel.refreshToken()
        } else {
            loginLauncher.launch(Intent(this, LoginActivity::class.java))
        }
    }
    
    private fun determineUserRoleAndNavigate() {
        authViewModel.loadStoredUserInfo()
        
        val storedRole = authViewModel.getUserRole()
        
        if (storedRole != null) {
            navigateBasedOnRole()
        } else {
            // No stored role - this means either:
            // 1. Backend didn't return user info and we need to determine role
            // 2. Role determination is in progress (async)
            waitForRoleOrFallback()
        }
    }
    
    private fun waitForRoleOrFallback() {
        // Simple single check with short delay to allow async role determination
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val role = authViewModel.getUserRole()
            
            if (role != null) {
                navigateBasedOnRole()
            } else {
                Log.w("MainActivity", "⚠️ No role found, using fallback detection")
                determineRoleByAdminAccess()
            }
        }, 1500)
    }
    
    private fun determineRoleByAdminAccess() {
        val username = authViewModel.getCurrentUser()?.username ?: ""
        
        if (username.isBlank()) {
            loginLauncher.launch(Intent(this, LoginActivity::class.java))
            return
        }
        
        if (shouldForceAdminRole(username)) {
            authViewModel.setUserRole(RoleDTO.Admin)
            goToAdminDashboard()
            return
        }
        
        val isLikelyAdmin = username.contains("admin", ignoreCase = true) || 
                           username == "admin" || 
                           username.lowercase().startsWith("admin") ||
                           isLikelyFirstUser(username)
                           
        val isLikelyCasher = username.contains("casher", ignoreCase = true) ||
                            username == "casher" ||
                            username.lowercase().startsWith("casher") ||
                            username.contains("cashier", ignoreCase = true)
        
        when {
            isLikelyAdmin -> {
                authViewModel.setUserRole(RoleDTO.Admin)
                goToAdminDashboard()
            }
            isLikelyCasher -> {
                authViewModel.setUserRole(RoleDTO.Cashier)
                goToCashierDashboard()
            }
            else -> {
                authViewModel.setUserRole(RoleDTO.Customer)
                goToProductList()
            }
        }
    }
    
    private fun shouldForceAdminRole(username: String): Boolean {
        if (!BuildConfig.DEBUG) return false
        if (!BuildConfig.FORCE_ADMIN_ROLE) return false
        
        val overrideUsername = BuildConfig.FORCE_ADMIN_USERNAME.trim()
        if (overrideUsername.isEmpty()) {
            return true
        }
        
        return overrideUsername.equals(username, ignoreCase = true)
    }
    
    private fun isLikelyFirstUser(username: String): Boolean {
        return username.length < 10 && 
               !username.contains("user", ignoreCase = true) && 
               !username.contains("customer", ignoreCase = true)
    }

    private fun navigateBasedOnRole() {
        val userRole = authViewModel.getUserRole()
        
        when {
            authViewModel.isAdmin() -> goToAdminDashboard()
            authViewModel.isCashier() -> goToCashierDashboard()
            authViewModel.isCustomer() -> goToProductList()
            else -> {
                Log.w("MainActivity", "Unknown user role: $userRole, defaulting to ProductList")
                goToProductList()
            }
        }
    }

    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToCashierDashboard() {
        val intent = Intent(this, CashierDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToProductList() {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
