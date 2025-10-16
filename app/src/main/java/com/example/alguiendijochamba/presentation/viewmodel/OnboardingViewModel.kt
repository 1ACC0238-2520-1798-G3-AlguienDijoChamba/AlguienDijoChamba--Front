package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {
    // Ejemplo: Puedes tener un estado para saber si el onboarding ya se mostró
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    fun completeOnboarding() {
        _onboardingCompleted.value = true
        // Aquí podrías guardar esto en SharedPreferences o DataStore
        // para que la próxima vez que el usuario abra la app no vea el onboarding
    }
}