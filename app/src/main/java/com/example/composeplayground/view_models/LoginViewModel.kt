package com.example.composeplayground.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("abc")
        private set

    var password by mutableStateOf("123")
        private set


    private val _uiState = MutableStateFlow<UIState?>(null)
    val uiState: StateFlow<UIState?> = _uiState

    var isLogin by mutableStateOf(false)
        private set

    fun updateUsername(newValue: String) {
        username = newValue
    }

    fun updatePassword(newValue: String) {
        password = newValue
    }

    fun validateUser(username: String, password: String) = viewModelScope.launch {
        _uiState.value = UIState.Loading

        delay(1000)

        if (username == "abc" && password == "123") {
            isLogin = true
            _uiState.value = UIState.Success("Login Success")
            return@launch
        }
        _uiState.value = UIState.Failure("Login Failed")
    }
}


sealed class UIState {
    data class Success(val message: String) : UIState()
    data class Failure(val message: String) : UIState()
    object Loading : UIState()
}