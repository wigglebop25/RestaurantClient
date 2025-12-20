package com.restaurantclient.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.UpdateUserRequest
import com.restaurantclient.data.dto.UserDTO
import com.restaurantclient.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Result<UserDTO>>()
    val userProfile: LiveData<Result<UserDTO>> = _userProfile

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getUserById(userId)
                _userProfile.value = result
            } catch (e: Exception) {
                _userProfile.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUserProfile(userId: Int, username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val updateUserRequest = UpdateUserRequest(username, password)
                val result = userRepository.updateUserProfile(userId, updateUserRequest)
                _updateResult.value = result
            } catch (e: Exception) {
                _updateResult.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }
}
