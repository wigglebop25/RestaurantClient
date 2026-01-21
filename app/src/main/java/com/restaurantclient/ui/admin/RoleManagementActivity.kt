package com.restaurantclient.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.MainActivity
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.databinding.ActivityRoleManagementBinding
import com.restaurantclient.ui.common.setupGlassEffect
import com.restaurantclient.util.ToastManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoleManagementActivity : BaseAdminActivity() {

    private lateinit var binding: ActivityRoleManagementBinding
    private val roleManagementViewModel: RoleManagementViewModel by viewModels()
    private lateinit var roleManagementAdapter: RoleManagementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGlassFAB()
        setupRecyclerView()
        setupClickListeners()
        setupObservers()

        // Load initial data
        roleManagementViewModel.loadRoles()
    }

    override fun onResume() {
        super.onResume()
        roleManagementViewModel.startPollingRoles()
    }

    override fun onPause() {
        super.onPause()
        roleManagementViewModel.stopPollingRoles()
    }

    private fun setupToolbar() {
        setupAdminToolbar(
            binding.adminToolbar.toolbar,
            "Role Management",
            showBackButton = true
        )
    }

    private fun setupGlassFAB() {
        binding.addRoleFabBlur.setupGlassEffect(25f)
    }

    private fun setupRecyclerView() {
        roleManagementAdapter = RoleManagementAdapter(
            onEditRole = { role ->
                showEditRoleDialog(role)
            },
            onAddPermission = { role ->
                showAddPermissionDialog(role)
            },
            onRemovePermission = { role ->
                showRemoveAllPermissionsConfirmation(role)
            },
            onDeleteRole = { role ->
                showDeleteConfirmationDialog(role.name) {
                    roleManagementViewModel.deleteRole(role.roleId)
                }
            }
        )

        binding.rolesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoleManagementActivity)
            adapter = roleManagementAdapter
        }
    }

    private fun showRemoveAllPermissionsConfirmation(role: RoleDetailsDTO) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Remove All Permissions")
            .setMessage("Are you sure you want to remove all permissions from role '${role.name}'?")
            .setPositiveButton("Remove All") { _, _ ->
                roleManagementViewModel.removePermissionFromRole(role.roleId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupClickListeners() {
        binding.addRoleFab.setOnClickListener {
            showCreateRoleDialog()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            roleManagementViewModel.loadRoles()
        }
    }

    private fun setupObservers() {
        roleManagementViewModel.roles.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    roleManagementAdapter.submitList(result.data)

                    if (result.data.isEmpty()) {
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        binding.rolesRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.rolesRecyclerView.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    ToastManager.showToast(this, "Failed to load roles: $message", android.widget.Toast.LENGTH_LONG)
                }
            }
        }

        roleManagementViewModel.createRoleResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    ToastManager.showToast(this, "Role created successfully")
                    roleManagementViewModel.loadRoles()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    ToastManager.showToast(this, "Failed to create role: $message", android.widget.Toast.LENGTH_LONG)
                }
            }
        }

        roleManagementViewModel.updateRoleResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    ToastManager.showToast(this, "Role updated successfully")
                    roleManagementViewModel.loadRoles()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    ToastManager.showToast(this, "Failed to update role: $message", android.widget.Toast.LENGTH_LONG)
                }
            }
        }

        roleManagementViewModel.deleteRoleResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    ToastManager.showToast(this, "Role deleted successfully")
                    roleManagementViewModel.loadRoles()
                }
                is Result.Error -> {
                    val message = com.restaurantclient.util.ErrorUtils.getHumanFriendlyErrorMessage(result.exception)
                    ToastManager.showToast(this, "Failed to delete role: $message", android.widget.Toast.LENGTH_LONG)
                }
            }
        }

        roleManagementViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showCreateRoleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_role, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.role_name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.role_description_input)

        MaterialAlertDialogBuilder(this)
            .setTitle("Create New Role")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()

                if (name.isNotEmpty()) {
                    roleManagementViewModel.createRole(name, description)
                } else {
                    ToastManager.showToast(this, "Role name cannot be empty")
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showEditRoleDialog(role: RoleDetailsDTO) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_role, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.role_name_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.role_description_input)

        nameInput.setText(role.name)
        descriptionInput.setText(role.description)

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Role: ${role.name}")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = nameInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()

                if (name.isNotEmpty()) {
                    roleManagementViewModel.updateRole(role.roleId, name, description)
                } else {
                    ToastManager.showToast(this, "Role name cannot be empty")
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showAddPermissionDialog(role: RoleDetailsDTO) {
        val availablePermissions = arrayOf("ADMIN", "READ", "WRITE", "DELETE")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Permission to ${role.name}")
            .setItems(availablePermissions) { _, which ->
                val permission = availablePermissions[which]
                roleManagementViewModel.addPermissionToRole(role.name, permission)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(roleName: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Delete Role")
            .setMessage("Are you sure you want to delete role '$roleName'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.role_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_refresh -> {
                roleManagementViewModel.loadRoles()
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
