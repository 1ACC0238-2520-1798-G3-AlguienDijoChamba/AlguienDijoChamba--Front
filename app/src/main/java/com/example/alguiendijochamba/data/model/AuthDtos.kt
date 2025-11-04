package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("Email")
    val email: String,

    @SerializedName("Password")
    val password: String,

    @SerializedName("Dni")
    val dni: String,

    @SerializedName("Nombres")
    val nombres: String,

    @SerializedName("Apellidos")
    val apellidos: String,

    @SerializedName("Celular")
    val celular: String
)

data class RegisterResponseDto(
    @SerializedName("userId")
    val userId: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    @SerializedName("token")
    val token: String
)