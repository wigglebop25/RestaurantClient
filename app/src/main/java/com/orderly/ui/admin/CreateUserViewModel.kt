package com.orderly.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderly.data.Result
import com.orderly.data.dto.RoleDTO
import com.orderly.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _createUserResult = MutableLiveData<Result<Unit>>()
    val createUserResult: LiveData<Result<Unit>> = _createUserResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun createUser(username: String, password: String, role: RoleDTO) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.createUser(username, password, role)
                _createUserResult.value = result
            } catch (e: Exception) {
                _createUserResult.value = Result.Error(e)
            } finally {
                _loading.value = false
            }
        }
    }
}