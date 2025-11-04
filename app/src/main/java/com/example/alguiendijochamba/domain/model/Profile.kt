package com.example.alguiendijochamba.domain.model

import java.util.Date

// Modelo de dominio "limpio" para el perfil
data class Profile(
    val userName: String,
    val professionalLevel: String,
    val starRating: Double,
    val completedJobs: Int,
    val availableBalance: Double,
    val nombres: String,
    val apellidos: String,
    val ocupacion: String,
    val email: String,
    val celular: String,
    val fechaNacimiento: Date?,
    val genero: String?,
    val fotoPerfilUrl: String?
)
