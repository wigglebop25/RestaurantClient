package com.restaurantclient.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantclient.R
import com.restaurantclient.data.dto.RoleDetailsDTO
import com.restaurantclient.databinding.ItemRoleBinding
import com.restaurantclient.ui.common.setupGlassEffect

class RoleManagementAdapter(
    private val onEditRole: (RoleDetailsDTO) -> Unit,
    private val onManagePermissions: (RoleDetailsDTO) -> Unit,
    private val onDeleteRole: (RoleDetailsDTO) -> Unit
) : ListAdapter<RoleDetailsDTO, RoleManagementAdapter.RoleViewHolder>(RoleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val binding = ItemRoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoleViewHolder(private val binding: ItemRoleBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setupBlur()
        }

        private fun setupBlur() {
            val context = binding.root.context
            val whiteOverlay = ContextCompat.getColor(context, R.color.white_glass_overlay)
            binding.roleCardBlur.setOverlayColor(whiteOverlay)
            binding.roleCardBlur.setupGlassEffect(20f)
        }

        fun bind(role: RoleDetailsDTO) {
            binding.roleNameText.text = role.name
            binding.roleDescriptionText.text = role.description ?: "No description"
            
            // Display permissions
            val permissions = role.permissions ?: emptyList()
            if (permissions.isNotEmpty()) {
                binding.permissionsBadge.text = "${permissions.size} permissions: ${permissions.joinToString(", ")}"
            } else {
                binding.permissionsBadge.text = "No permissions"
            }
            
            // Set role badge color
            when (role.name.uppercase()) {
                "ADMIN" -> {
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_primary))
                }
                "CUSTOMER" -> {
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_secondary))
                }
                "CASHER" -> {
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_success))
                }
                else -> {
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_accent))
                }
            }
            binding.roleBadge.text = role.name.uppercase()

            // Set click listeners
            binding.editButton.setOnClickListener {
                onEditRole(role)
            }

            binding.managePermissionsButton.setOnClickListener {
                onManagePermissions(role)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteRole(role)
            }

            // Don't allow deletion of core roles
            binding.deleteButton.isEnabled = role.name.uppercase() !in listOf("ADMIN", "CUSTOMER", "CASHER")
        }
    }

    class RoleDiffCallback : DiffUtil.ItemCallback<RoleDetailsDTO>() {
        override fun areItemsTheSame(oldItem: RoleDetailsDTO, newItem: RoleDetailsDTO): Boolean {
            return oldItem.roleId == newItem.roleId
        }

        override fun areContentsTheSame(oldItem: RoleDetailsDTO, newItem: RoleDetailsDTO): Boolean {
            return oldItem == newItem
        }
    }
}
