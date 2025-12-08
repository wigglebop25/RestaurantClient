package com.orderly.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.orderly.MainActivity
import com.orderly.R
import com.orderly.data.Result
import com.orderly.databinding.ActivityUserManagementBinding
import com.orderly.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManagementBinding
    private val userManagementViewModel: UserManagementViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userManagementAdapter: UserManagementAdapter

    private val createUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh user list after creating new user
            userManagementViewModel.loadUsers()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        // Load initial data
        userManagementViewModel.loadUsers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "User Management"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        userManagementAdapter = UserManagementAdapter(
            onEditUser = { user ->
                // TODO: Implement edit user functionality
                Toast.makeText(this, "Edit user: ${user.username}", Toast.LENGTH_SHORT).show()
            },
            onDeleteUser = { user ->
                showDeleteConfirmationDialog(user.username) {
                    userManagementViewModel.deleteUser(user.userId ?: 0)
                }
            }
        )

        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserManagementActivity)
            adapter = userManagementAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addUserFab.setOnClickListener {
            createUserLauncher.launch(Intent(this, CreateUserActivity::class.java))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            userManagementViewModel.loadUsers()
        }
    }

    private fun setupObservers() {
        userManagementViewModel.users.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    userManagementAdapter.submitList(result.data)
                    
                    if (result.data.isEmpty()) {
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        binding.usersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, "Failed to load users: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        userManagementViewModel.deleteUserResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    userManagementViewModel.loadUsers() // Refresh list
                }
                is Result.Error -> {
                    Toast.makeText(this, "Failed to delete user: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        userManagementViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showDeleteConfirmationDialog(username: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete user '$username'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_refresh -> {
                userManagementViewModel.loadUsers()
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
}