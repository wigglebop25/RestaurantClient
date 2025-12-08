package com.orderly.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.dto.UserDTO
import com.orderly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dashboardStats = MutableLiveData<DashboardStats>()
    val dashboardStats: LiveData<DashboardStats> = _dashboardStats

    private val _recentUsers = MutableLiveData<List<UserDTO>>()
    val recentUsers: LiveData<List<UserDTO>> = _recentUsers

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            
            try {
                // Load users data (this will be used to calculate stats)
                val usersResult = userRepository.getAllUsers()
                
                when (usersResult) {
                    is Result.Success -> {
                        val users = usersResult.data
                        
                        // Calculate dashboard statistics
                        val stats = DashboardStats(
                            totalUsers = users.size,
                            totalOrders = 0, // TODO: Get from orders repository when available
                            totalProducts = 0, // TODO: Get from products repository when available
                            newUsersToday = calculateNewUsersToday(users)
                        )
                        
                        _dashboardStats.value = stats
                        _recentUsers.value = users.takeLast(5) // Show 5 most recent users
                    }
                    is Result.Error -> {
                        _error.value = "Failed to load dashboard data: ${usersResult.exception.message}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun calculateNewUsersToday(users: List<UserDTO>): Int {
        // TODO: Implement actual date filtering when createdAt is properly formatted
        // For now, return 0 as placeholder
        return 0
    }

    fun refreshData() {
        loadDashboardData()
    }
}

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val totalProducts: Int,
    val newUsersToday: Int
)