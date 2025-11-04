package com.example.alguiendijochamba.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alguiendijochamba.data.local.SessionManager

// Esta clase le dice a Android cómo crear una instancia de SignInViewModel
// pasándole el SessionManager que necesita.
class SignInViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            // Pasamos el SessionManager al constructor del ViewModel
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

