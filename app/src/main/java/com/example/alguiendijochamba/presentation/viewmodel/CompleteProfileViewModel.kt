package com.example.alguiendijochamba.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CompleteProfileUiState(
    val profilePhotoUri: Uri? = null,
    val specialties: List<String> = emptyList(), // Las que el usuario selecciona
    val availableSpecialties: List<String> = listOf("Gasfitería", "Electricidad", "Carpintería", "Pintura", "Albañilería"), // Simulación desde el backend
    val yearsOfExperience: String = "",
    val hourlyRate: String = "",
    val professionalBio: String = "",
    val certificationUris: List<Uri> = emptyList(),
    // Errores de validación
    val experienceError: String? = null,
    val bioError: String? = null
)

class CompleteProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState = _uiState.asStateFlow()

    // --- Funciones para actualizar el estado desde la UI ---
    fun onPhotoSelected(uri: Uri?) {
        _uiState.update { it.copy(profilePhotoUri = uri) }
    }

    fun onSpecialtyAdded(specialty: String) {
        _uiState.update {
            val updatedList = it.specialties + specialty
            it.copy(specialties = updatedList.distinct()) // distinct() para no duplicar
        }
    }

    fun onSpecialtyRemoved(specialty: String) {
        _uiState.update {
            val updatedList = it.specialties - specialty
            it.copy(specialties = updatedList)
        }
    }

    fun onExperienceChange(years: String) {
        _uiState.update { it.copy(yearsOfExperience = years, experienceError = null) }
    }

    fun onRateChange(rate: String) {
        _uiState.update { it.copy(hourlyRate = rate) }
    }

    fun onBioChange(bio: String) {
        if (bio.length <= 1200) {
            _uiState.update { it.copy(professionalBio = bio, bioError = null) }
        }
    }

    fun onCertificationSelected(uri: Uri?) {
        uri?.let {
            _uiState.update { state ->
                state.copy(certificationUris = state.certificationUris + it)
            }
        }
    }

    // --- Lógica de negocio ---
    fun onSaveProfile() {
        val experience = _uiState.value.yearsOfExperience
        val bio = _uiState.value.professionalBio

        val isExperienceValid = experience.isNotBlank() && experience.toIntOrNull() != null
        val isBioValid = bio.isNotBlank()

        if (!isExperienceValid) {
            _uiState.update { it.copy(experienceError = "Campo obligatorio") }
        }
        if (!isBioValid) {
            _uiState.update { it.copy(bioError = "Campo obligatorio") }
        }

        if (isExperienceValid && isBioValid) {
            println("¡Perfil guardado con éxito!")
            // Aquí llamarías al UseCase para subir los datos al backend
        }
    }
}