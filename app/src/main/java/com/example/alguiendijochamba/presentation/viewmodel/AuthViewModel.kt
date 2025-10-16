// presentation/viewmodel/AuthViewModel.kt
package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Estado para saber si el usuario se está registrando o ya tiene una cuenta
    private val _isRegistering = MutableStateFlow(true)
    val isRegistering: StateFlow<Boolean> = _isRegistering.asStateFlow()

    // Manejar el clic en "I already have an account"
    fun toggleAuthMode() {
        _isRegistering.value = !_isRegistering.value
        // Aquí podrías limpiar errores o estados relacionados con el registro/login
    }

    // Funciones para interactuar con el backend (ejemplos)
    fun registerAsProfessional() {
        viewModelScope.launch {
            // Lógica de registro. Aquí llamarías a un UseCase (RegisterUserUseCase)
            // val result = registerUserUseCase.execute()
            // if (result.isSuccess) { ... } else { ... }
            println("Intentando registrar como profesional...")
        }
    }

    fun login() {
        viewModelScope.launch {
            // Lógica de login
            println("Intentando iniciar sesión...")
        }
    }
}