package com.btech_dev.quotebro.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btech_dev.quotebro.data.model.Profile
import com.btech_dev.quotebro.data.repository.AuthRepository
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isCheckingAuth: Boolean = true,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val userProfile: Profile? = null,
    val userEmail: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                // Restore session from storage
                val restored = repository.loadSessionFromStorage()
                // Also check if already logged in (memory)
                val isLoggedIn = restored || repository.isUserLoggedIn()

                if (isLoggedIn) {
                    fetchUserProfile()
                }

                _uiState.update {
                    it.copy(isCheckingAuth = false, isAuthenticated = isLoggedIn)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCheckingAuth = false, isAuthenticated = false)
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.signIn(email, password)
                fetchUserProfile()
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            } catch (e: Exception) {
                if (e is RestException) {
                    _uiState.update { it.copy(isLoading = false, error = e.description) }
                } else _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.signUp(email, password, fullName)
                // Depending on Supabase settings, user might need to confirm email.
                // For now, assume auto-login or just success message.
                // We'll try to sign in or just set authenticated if session is active.
                if (repository.isUserLoggedIn()) {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Please check your email to confirm registration"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.update { it.copy(isAuthenticated = false) }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getCurrentProfile()
                val email = repository.getCurrentUserEmail()
                _uiState.update { it.copy(userProfile = profile, userEmail = email) }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
