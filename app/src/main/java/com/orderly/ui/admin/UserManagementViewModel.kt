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
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<Result<List<UserDTO>>>()
    val users: LiveData<Result<List<UserDTO>>> = _users

    private val _deleteUserResult = MutableLiveData<Result<Unit>>()
    val deleteUserResult: LiveData<Result<Unit>> = _deleteUserResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUsers() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getAllUsers()
                _users.value = result
            } catch (e: Exception) {
                _users.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                val result = userRepository.deleteUser(userId)
                _deleteUserResult.value = result
            } catch (e: Exception) {
                _deleteUserResult.value = Result.Error(e)
            }
        }
    }

    fun refreshUsers() {
        loadUsers()
    }
}