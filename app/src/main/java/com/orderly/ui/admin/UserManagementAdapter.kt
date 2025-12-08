package com.orderly.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orderly.R
import com.orderly.data.dto.RoleDTO
import com.orderly.data.dto.UserDTO
import com.orderly.databinding.ItemUserBinding

class UserManagementAdapter(
    private val onEditUser: (UserDTO) -> Unit,
    private val onDeleteUser: (UserDTO) -> Unit
) : ListAdapter<UserDTO, UserManagementAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserDTO) {
            binding.usernameText.text = user.username
            binding.createdAtText.text = user.createdAt ?: "Unknown"
            
            // Set role badge
            when (user.role) {
                RoleDTO.Admin -> {
                    binding.roleBadge.text = "ADMIN"
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_primary))
                }
                RoleDTO.Customer -> {
                    binding.roleBadge.text = "CUSTOMER"
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_secondary))
                }
                else -> {
                    binding.roleBadge.text = "UNKNOWN"
                    binding.roleBadge.setBackgroundColor(binding.root.context.getColor(R.color.admin_text_secondary))
                }
            }

            // Set click listeners
            binding.editButton.setOnClickListener {
                onEditUser(user)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteUser(user)
            }

            // Don't allow deletion of admin users (safety measure)
            binding.deleteButton.isEnabled = user.role != RoleDTO.Admin
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserDTO>() {
        override fun areItemsTheSame(oldItem: UserDTO, newItem: UserDTO): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserDTO, newItem: UserDTO): Boolean {
            return oldItem == newItem
        }
    }
}