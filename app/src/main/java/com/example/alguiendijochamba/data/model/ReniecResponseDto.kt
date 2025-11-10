package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class ReniecResponseDto(
    @SerializedName("nombres")
    val nombres: String?,

    // Soporta apellidos como un solo campo (si el backend los envía juntos)
    @SerializedName(value = "apellidos", alternate = ["Apellidos"])
    val apellidos: String?,

    // También soporta apellidos separados (por compatibilidad)
    @SerializedName(value = "apellidoPaterno", alternate = ["apellido_paterno"])
    val apellidoPaterno: String?,

    @SerializedName(value = "apellidoMaterno", alternate = ["apellido_materno"])
    val apellidoMaterno: String?
)