package com.orderly.ui.admin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orderly.R
import com.orderly.data.Result
import com.orderly.data.dto.RoleDTO
import com.orderly.databinding.ActivityCreateUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private val createUserViewModel: CreateUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRoleSpinner()
        setupClickListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Create New User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Customer", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter
        // Default to Customer role
        binding.roleSpinner.setSelection(0)
    }

    private fun setupClickListeners() {
        binding.createUserButton.setOnClickListener {
            createUser()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        createUserViewModel.createUserResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to create user: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        createUserViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.createUserButton.isEnabled = !isLoading
        }
    }

    private fun createUser() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // Validation
        if (username.isEmpty()) {
            binding.usernameLayout.error = "Username is required"
            return
        } else {
            binding.usernameLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return
        } else {
            binding.passwordLayout.error = null
        }

        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            return
        } else {
            binding.confirmPasswordLayout.error = null
        }

        if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            return
        } else {
            binding.passwordLayout.error = null
        }

        // Determine role
        val role = when (binding.roleSpinner.selectedItem.toString()) {
            "Admin" -> RoleDTO.Admin
            "Customer" -> RoleDTO.Customer
            else -> RoleDTO.Customer
        }

        // Create user
        binding.progressBar.visibility = View.VISIBLE
        createUserViewModel.createUser(username, password, role)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}