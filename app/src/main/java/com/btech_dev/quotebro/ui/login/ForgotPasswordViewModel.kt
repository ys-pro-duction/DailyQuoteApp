package com.btech_dev.quotebro.ui.login

import androidx.compose.animation.scaleOut
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btech_dev.quotebro.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailSent: Boolean = false,
    val passwordUpdated: Boolean = false
)

class ForgotPasswordViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun requestPasswordReset(email: String) {
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.resetPasswordForEmail(email,
                    "http://reset-callback.quotebro")
                _uiState.update { 
                    it.copy(isLoading = false, emailSent = true, error = null) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Failed to send reset email"
                    ) 
                }
            }
        }
    }

    fun updatePassword(redirectUrl: String,newPassword: String, confirmPassword: String) {
        if (!isValidPassword(newPassword, confirmPassword)) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.updatePassword(redirectUrl, newPassword)
                _uiState.update { 
                    it.copy(isLoading = false, passwordUpdated = true, error = null) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Failed to update password"
                    )
                }
                println(e.message)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String, confirmPassword: String): Boolean {
        when {
            password.length < 6 -> {
                _uiState.update { it.copy(error = "Password must be at least 6 characters") }
                return false
            }
            password != confirmPassword -> {
                _uiState.update { it.copy(error = "Passwords do not match") }
                return false
            }
            else -> return true
        }
    }
}
