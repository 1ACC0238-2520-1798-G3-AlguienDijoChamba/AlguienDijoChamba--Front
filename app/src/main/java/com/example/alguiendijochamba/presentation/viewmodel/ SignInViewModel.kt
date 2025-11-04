package com.example.alguiendijochamba.presentation.viewmodel

// 1. Quita la importación de Application
import androidx.lifecycle.ViewModel // 2. Cambia de AndroidViewModel a ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.local.SessionManager
import com.example.alguiendijochamba.data.model.LoginRequestDto
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalErrorMessage: String? = null
)

// 3. Ya no es AndroidViewModel y recibe las dependencias en el constructor
class SignInViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()

    // 4. ¡Estas líneas se eliminan! Koin las provee.
    // private val sessionManager = SessionManager(application)
    // private val userRepository = UserRepositoryImpl(application)


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
            _uiState.update { it.copy(emailError = "Correo Inválido") }
            return false
        }
        return true
    }

    fun onSignInClicked(onSuccess: (String) -> Unit) {
        _uiState.update { it.copy(generalErrorMessage = null) }

        val isEmailValid = validateEmail()
        val isPasswordValid = _uiState.value.password.isNotBlank()

        if (!isPasswordValid) {
            _uiState.update { it.copy(generalErrorMessage = "La contraseña no puede estar vacía.") }
            return
        }

        if (isEmailValid && isPasswordValid) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val request = LoginRequestDto(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                val result = userRepository.loginUser(request)

                result.onSuccess { token ->
                    println("Login exitoso! Guardando token...")
                    sessionManager.saveAuthToken(token) // Guardamos el token
                    onSuccess(token) // Devolvemos el token a la vista
                }.onFailure { error ->
                    println("Error en el login: ${error.message}")
                    _uiState.update { it.copy(generalErrorMessage = "Credenciales incorrectas.") }
                }

                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}