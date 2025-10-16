package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class ReniecResponseDto(
    @SerializedName("nombres")
    val nombres: String?,
    @SerializedName("apellidoPaterno")
    val apellidoPaterno: String?,
    @SerializedName("apellidoMaterno")
    val apellidoMaterno: String?
)