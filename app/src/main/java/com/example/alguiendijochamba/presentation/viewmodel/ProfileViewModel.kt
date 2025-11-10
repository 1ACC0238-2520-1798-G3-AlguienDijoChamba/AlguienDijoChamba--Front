package com.example.alguiendijochamba.presentation.viewmodel

import android.net.Uri // Necesario para la foto de perfil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.data.model.UpdateProfileRequestDto
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
    val celular: String = "",
    val emailError: String? = null,
    val celularError: String? = null,
    val saveError: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

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

    fun onEditToggle() = _uiState.update {
        it.copy(
            isEditing = !it.isEditing,
            // Limpia mensajes de error al cambiar de modo
            emailError = null,
            celularError = null,
            saveError = null
        )
    }

    // --- CAMBIOS DE INPUT CON LIMPIEZA DE ERRORES ---
    fun onOcupacionChange(ocupacion: String) = _uiState.update { it.copy(ocupacion = ocupacion, saveError = null) }
    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, emailError = null, saveError = null) }
    fun onCelularChange(celular: String) {
        val filteredCelular = celular.filter { it.isDigit() }

        if (filteredCelular.length <= 9) {
            _uiState.update { it.copy(celular = filteredCelular, celularError = null, saveError = null) }
        }
    }
    fun onPhotoSelected(uri: Uri?) {
        println("Foto seleccionada, debe iniciar la subida al backend.")
    }

    fun onPhotoDelete() {
        // Lógica para enviar una solicitud al backend para eliminar o limpiar la foto
        // Nota: El backend necesitaría un endpoint para limpiar la URL de la foto.
        _uiState.update { it.copy(fotoPerfilUrl = null) }
        println("Foto de perfil marcada para eliminación/limpieza en el backend.")
    }


    fun onSaveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value

            val isEmailValid = currentState.email.contains("@") && currentState.email.isNotBlank()
            // 2. VALIDA LONGITUD EXACTA AQUÍ
            val isCelularValid = currentState.celular.length == 9 && currentState.celular.all { it.isDigit() }

            if (!isEmailValid) {
                _uiState.update { it.copy(emailError = "Formato de email inválido.") }
            }
            if (!isCelularValid) {
                // Muestra un error específico si el celular no tiene 9 dígitos
                _uiState.update { it.copy(celularError = "El celular debe tener exactamente 9 dígitos.") }
            }

            if (!isEmailValid || !isCelularValid) {
                _uiState.update { it.copy(saveError = "Revise los campos obligatorios.") }
                return@launch
            }

            // 2. CONSTRUCCIÓN DEL REQUEST
            val updateRequest = UpdateProfileRequestDto(
                // Solo enviamos los campos que el usuario puede editar según tu requerimiento
                ocupacion = currentState.ocupacion,
                email = currentState.email,
                celular = currentState.celular,
                // El backend maneja el URL de la foto por separado o en un DTO completo.
                // Aquí usamos el DTO existente, que permite nulos:
                fotoPerfilUrl = currentState.fotoPerfilUrl
            )

            // 3. LLAMADA AL REPOSITORIO
            userRepository.updateMyProfile(updateRequest).onSuccess {
                _uiState.update { it.copy(isEditing = false, saveError = null) } // Sale de edición y limpia errores
                loadProfile() // Recarga los datos actualizados
                println("Perfil actualizado exitosamente")
            }.onFailure { error ->
                println("Error al guardar perfil: ${error.message}")
                _uiState.update { it.copy(saveError = error.message ?: "Error al guardar, intente de nuevo.") }
            }
        }
    }

    fun onDeleteProfile(onSuccessDelete: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteAccount().onSuccess {
                _uiState.update { it.copy(profileDeleted = true) }
                println("Cuenta eliminada exitosamente")
                onSuccessDelete() // Dispara la navegación de regreso al inicio
            }.onFailure { error ->
                println("Error al eliminar cuenta: ${error.message}")
                _uiState.update { it.copy(saveError = "Error al eliminar: ${error.message}") }
            }
        }
    }
}