// En: presentation/viewmodel/CompleteProfileViewModel.kt

package com.example.alguiendijochamba.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // <-- IMPORTANTE
import com.example.alguiendijochamba.data.model.CompleteProfileRequestDto
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch // <-- IMPORTANTE

data class CompleteProfileUiState(
    val profilePhotoUri: Uri? = null,
    val specialties: List<String> = emptyList(),
    val availableSpecialties: List<String> = listOf("Gasfitería", "Electricidad", "Carpintería", "Pintura", "Albañilería"),
    val yearsOfExperience: String = "",
    val hourlyRate: String = "",
    val professionalBio: String = "",
    val certificationUris: List<Uri> = emptyList(),
    // Errores de validación
    val experienceError: String? = null,
    val bioError: String? = null,
    // --- NUEVOS ESTADOS ---
    val isLoading: Boolean = false,
    val saveError: String? = null,
    val saveSuccess: Boolean = false
)

// --- CONSTRUCTOR ACTUALIZADO ---
class CompleteProfileViewModel(
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState = _uiState.asStateFlow()

    // ... (Funciones onPhotoSelected, onSpecialtyAdded, etc. se mantienen igual) ...
    fun onPhotoSelected(uri: Uri?) { _uiState.update { it.copy(profilePhotoUri = uri) } }
    fun onSpecialtyAdded(specialty: String) { _uiState.update { val list = it.specialties + specialty; it.copy(specialties = list.distinct()) } }
    fun onSpecialtyRemoved(specialty: String) { _uiState.update { val list = it.specialties - specialty; it.copy(specialties = list) } }
    fun onExperienceChange(years: String) { _uiState.update { it.copy(yearsOfExperience = years, experienceError = null) } }
    fun onRateChange(rate: String) { _uiState.update { it.copy(hourlyRate = rate) } }
    fun onBioChange(bio: String) { if (bio.length <= 1200) { _uiState.update { it.copy(professionalBio = bio, bioError = null) } } }
    fun onCertificationSelected(uri: Uri?) { uri?.let { _uiState.update { state -> state.copy(certificationUris = state.certificationUris + it) } } }


    // --- LÓGICA DE NEGOCIO ACTUALIZADA ---
    fun onSaveProfile() {
        val state = _uiState.value

        // 1. Validaciones (igual que antes)
        val experience = state.yearsOfExperience.toIntOrNull()
        val bio = state.professionalBio
        val rate = state.hourlyRate.toDoubleOrNull() // Permite que sea nulo

        val isExperienceValid = experience != null
        val isBioValid = bio.isNotBlank()

        if (!isExperienceValid) _uiState.update { it.copy(experienceError = "Campo obligatorio") }
        if (!isBioValid) _uiState.update { it.copy(bioError = "Campo obligatorio") }

        if (!isExperienceValid || !isBioValid) {
            return
        }

        // 2. Inicia la corrutina para guardar
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, saveError = null) }

            try {
                // 3. Sube la foto de perfil (si existe)
                val photoUrl: String? = state.profilePhotoUri?.let { uri ->
                    async { userRepository.uploadProfilePhoto(uri) }.await()
                        .getOrThrow() // Lanza excepción si falla
                }

                // 4. Sube las certificaciones (si existen) concurrentemente
                val certUrls: List<String> = state.certificationUris.map { uri ->
                    async { userRepository.uploadCertification(uri) }.await()
                        .getOrThrow() // Lanza excepción si falla
                }

                // 5. Construye el DTO para enviar al backend
                val requestDto = CompleteProfileRequestDto(
                    yearsOfExperience = experience!!,
                    hourlyRate = rate,
                    professionalBio = bio,
                    profilePhotoUrl = photoUrl,
                    certificationUrls = certUrls
                )

                // 6. Llama al endpoint final para guardar los datos
                userRepository.completeProfile(requestDto).getOrThrow()

                // 7. Éxito
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                println("¡Perfil guardado con éxito en el backend!")

            } catch (e: Exception) {
                // 8. Manejo de error
                println("Error al guardar perfil: ${e.message}")
                _uiState.update { it.copy(isLoading = false, saveError = e.message ?: "Error desconocido") }
            }
        }
    }
}