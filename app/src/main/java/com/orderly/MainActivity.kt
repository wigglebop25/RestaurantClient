package com.orderly

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.ui.auth.AuthViewModel
import com.orderly.ui.auth.LoginActivity
import com.orderly.ui.product.ProductListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            goToProductList()
        } else {
            // User cancelled login, so finish the app
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (authViewModel.isLoggedIn()) {
            goToProductList()
        } else {
            loginLauncher.launch(Intent(this, LoginActivity::class.java))
        }
    }

    private fun goToProductList() {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}