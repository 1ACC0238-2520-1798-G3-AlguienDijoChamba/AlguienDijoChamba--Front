package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * DTO para actualizar el perfil del usuario.
 * Contiene solo los campos que el usuario puede modificar.
 */
data class UpdateProfileRequestDto(
    @SerializedName("nombres") val nombres: String? = null,
    @SerializedName("apellidos") val apellidos: String? = null,
    @SerializedName("ocupacion") val ocupacion: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("celular") val celular: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: Date? = null,
    @SerializedName("genero") val genero: String? = null,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String? = null
)

