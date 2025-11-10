package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

// Esta clase DEBE coincidir con el JSON que envía tu backend
data class ProfileResponseDto(
    @SerializedName("userName") val userName: String,
    @SerializedName("professionalLevel") val professionalLevel: String,
    @SerializedName("starRating") val starRating: Double,
    @SerializedName("completedJobs") val completedJobs: Int,
    @SerializedName("availableBalance") val availableBalance: Double,

    // --- ESTOS SON LOS CAMPOS QUE PROBABLEMENTE TE FALTAN ---
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("ocupacion") val ocupacion: String,
    @SerializedName("email") val email: String,
    @SerializedName("celular") val celular: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: Date?,
    @SerializedName("genero") val genero: String?,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String?
)

