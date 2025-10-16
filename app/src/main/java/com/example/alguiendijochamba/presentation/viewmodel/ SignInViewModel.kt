package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Patterns

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalErrorMessage: String? = null
)

class SignInViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun validateEmail(): Boolean {
        val email = _uiState.value.email
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(emailError = "Correo Invalido") }
            return false
        }
        return true
    }

    fun onSignInClicked(onSuccess: () -> Unit) {
        _uiState.update { it.copy(generalErrorMessage = null) }

        val isEmailValid = validateEmail()
        val isPasswordValid = _uiState.value.password.isNotBlank()

        if (!isPasswordValid) {
            _uiState.update { it.copy(generalErrorMessage = "La contraseña no puede estar vacía.") }
        }

        if (isEmailValid && isPasswordValid) {
            println("Email válido. Iniciando sesión...")
            // Si el login es exitoso, llama a la acción de navegación
            onSuccess()
        }
    }
}