package com.restaurantclient.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.restaurantclient.MainActivity
import com.restaurantclient.ui.auth.AuthViewModel
import com.restaurantclient.util.ToastManager

abstract class BaseAdminActivity : AppCompatActivity() {

    protected val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enforceAdminAccess()
    }

    override fun onResume() {
        super.onResume()
        enforceAdminAccess()
    }

    protected fun setupAdminToolbar(toolbar: Toolbar, title: String, showBackButton: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
    }

    protected fun bindAdminOnlyView(view: View) {
        view.isVisible = authViewModel.isAdmin()
    }

    private fun enforceAdminAccess() {
        authViewModel.loadStoredUserInfo()
        if (!authViewModel.isAdmin()) {
            ToastManager.showToast(this, "Admin access required", android.widget.Toast.LENGTH_LONG)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
