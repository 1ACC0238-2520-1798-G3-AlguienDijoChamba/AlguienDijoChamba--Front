// En: presentation/viewmodel/CompleteProfileViewModel.kt

package com.example.alguiendijochamba.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.model.CompleteProfileRequestDto
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompleteProfileUiState(
    val profilePhotoUri: Uri? = null,
    val specialties: List<String> = emptyList(),
    val availableSpecialties: List<String> = listOf("Gasfitería", "Electricidad", "Carpintería", "Pintura", "Albañilería"),
    val yearsOfExperience: String = "",
    val hourlyRate: String = "",
    val professionalBio: String = "",
    val certificationUris: List<Uri> = emptyList(),

    // --- Errores de validación ---
    val experienceError: String? = null,
    val bioError: String? = null,
    val hourlyRateError: String? = null, // <--- NUEVO: Para validar el precio obligatorio

    // --- Estados de Carga/Guardado ---
    val isLoading: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)

class CompleteProfileViewModel(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState = _uiState.asStateFlow()

    // --- Eventos de UI ---
    fun onPhotoSelected(uri: Uri?) {
        _uiState.update { it.copy(profilePhotoUri = uri) }
    }

    fun onSpecialtyAdded(specialty: String) {
        _uiState.update {
            val list = it.specialties + specialty
            it.copy(specialties = list.distinct())
        }
    }

    fun onSpecialtyRemoved(specialty: String) {
        _uiState.update {
            val list = it.specialties - specialty
            it.copy(specialties = list)
        }
    }

    fun onExperienceChange(years: String) {
        _uiState.update { it.copy(yearsOfExperience = years, experienceError = null) }
    }

    fun onRateChange(rate: String) {
        // Limpiamos el error cuando el usuario escribe
        _uiState.update { it.copy(hourlyRate = rate, hourlyRateError = null) }
    }

    fun onBioChange(bio: String) {
        if (bio.length <= 1200) {
            _uiState.update { it.copy(professionalBio = bio, bioError = null) }
        }
    }

    fun onCertificationSelected(uri: Uri?) {
        uri?.let {
            _uiState.update { state -> state.copy(certificationUris = state.certificationUris + it) }
        }
    }

    // --- LÓGICA DE NEGOCIO PRINCIPAL ---
    fun onSaveProfile(onSuccessNavigation: () -> Unit) {
        val state = _uiState.value

        // 1. Validaciones
        val experience = state.yearsOfExperience.toIntOrNull()
        val bio = state.professionalBio
        val rate = state.hourlyRate.toDoubleOrNull()

        // Reglas de validación
        val isExperienceValid = experience != null
        val isBioValid = bio.isNotBlank()
        // 🚀 CRÍTICO: Validar que el precio exista y sea mayor a 0 para que Flutter lo muestre bien
        val isRateValid = rate != null && rate > 0

        // Actualizar errores en UI
        if (!isExperienceValid) _uiState.update { it.copy(experienceError = "Campo obligatorio (numérico)") }
        if (!isBioValid) _uiState.update { it.copy(bioError = "Campo obligatorio") }
        if (!isRateValid) _uiState.update { it.copy(hourlyRateError = "Ingrese una tarifa válida mayor a 0") }

        // Si alguna validación falla, detenemos el proceso
        if (!isExperienceValid || !isBioValid || !isRateValid) {
            return
        }

        // 2. Inicia la corrutina para guardar
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, saveError = null) }

            try {
                // 3. Subida de Foto (si existe)
                val photoUrl: String? = state.profilePhotoUri?.let { uri ->
                    async { userRepository.uploadProfilePhoto(uri) }.await()
                        .getOrThrow()
                }

                // 4. Subida de Certificaciones (paralelo)
                val certUrls: List<String> = state.certificationUris.map { uri ->
                    async { userRepository.uploadCertification(uri) }.await()
                        .getOrThrow()
                }

                // 5. Construir DTO
                // Nota: Enviamos 'rate' validado. Esto permitirá al backend crear la Reputación inicial con precio.
                val requestDto = CompleteProfileRequestDto(
                    yearsOfExperience = experience!!,
                    hourlyRate = rate,
                    professionalBio = bio,
                    profilePhotoUrl = photoUrl,
                    certificationUrls = certUrls
                )

                // 6. Enviar al Backend
                // (El backend ahora debería disparar CreateInitialReputationCommand internamente)
                userRepository.completeProfile(requestDto).getOrThrow()

                // 7. Éxito
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                println("¡Perfil guardado con éxito! Reputación inicial creada en Backend.")
                onSuccessNavigation()

            } catch (e: Exception) {
                // 8. Error
                e.printStackTrace()
                println("Error al guardar perfil: ${e.message}")
                _uiState.update {
                    it.copy(isLoading = false, saveError = e.message ?: "Error desconocido al guardar perfil")
                }
            }
        }
    }
}