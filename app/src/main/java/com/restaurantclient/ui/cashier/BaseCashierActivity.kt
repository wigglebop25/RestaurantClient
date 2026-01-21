package com.restaurantclient.ui.cashier

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.restaurantclient.MainActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.util.ToastManager
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseCashierActivity : AppCompatActivity() {

    protected val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enforceCashierAccess()
    }

    override fun onResume() {
        super.onResume()
        enforceCashierAccess()
    }

    protected fun setupCashierToolbar(toolbar: Toolbar, title: String, showBackButton: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
    }

    private fun enforceCashierAccess() {
        authViewModel.loadStoredUserInfo()
        if (!authViewModel.isCashier()) {
            ToastManager.showToast(this, "Cashier access required", android.widget.Toast.LENGTH_LONG)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}