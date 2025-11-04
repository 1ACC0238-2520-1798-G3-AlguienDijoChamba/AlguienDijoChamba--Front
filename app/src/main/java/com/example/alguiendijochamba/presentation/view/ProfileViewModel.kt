package com.example.alguiendijochamba.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val profileDeleted: Boolean = false,
    val fotoPerfilUrl: String? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val ocupacion: String = "",
    val email: String = "",
    val celular: String = ""
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private val userRepository = UserRepositoryImpl(application)

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getMyProfile().onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombres = profile.nombres,
                        apellidos = profile.apellidos,
                        ocupacion = profile.ocupacion,
                        email = profile.email,
                        celular = profile.celular,
                        fotoPerfilUrl = profile.fotoPerfilUrl
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEditToggle() = _uiState.update { it.copy(isEditing = !it.isEditing) }
    fun onOcupacionChange(ocupacion: String) = _uiState.update { it.copy(ocupacion = ocupacion) }
    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email) }
    fun onCelularChange(celular: String) = _uiState.update { it.copy(celular = celular) }

    fun onSaveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val updateRequest = com.example.alguiendijochamba.data.model.UpdateProfileRequestDto(
                nombres = currentState.nombres,
                apellidos = currentState.apellidos,
                ocupacion = currentState.ocupacion,
                email = currentState.email,
                celular = currentState.celular,
                fotoPerfilUrl = currentState.fotoPerfilUrl
            )

            userRepository.updateMyProfile(updateRequest).onSuccess {
                _uiState.update { it.copy(isEditing = false) }
                println("Perfil actualizado exitosamente")
            }.onFailure { error ->
                println("Error al guardar perfil: ${error.message}")
                _uiState.update { it.copy(isEditing = false) }
            }
        }
    }

    fun onDeleteProfile() {
        viewModelScope.launch {
            userRepository.deleteAccount().onSuccess {
                _uiState.update { it.copy(profileDeleted = true) }
                println("Cuenta eliminada exitosamente")
            }.onFailure { error ->
                println("Error al eliminar cuenta: ${error.message}")
            }
        }
    }
}