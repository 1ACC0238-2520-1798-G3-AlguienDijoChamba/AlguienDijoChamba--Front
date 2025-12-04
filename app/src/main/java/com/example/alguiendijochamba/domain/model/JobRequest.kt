package com.example.alguiendijochamba.domain.model

import com.google.gson.annotations.SerializedName

data class JobRequest(
    val id: String,
    val clientId: String,
    val professionalId: String,
    val specialty: String,
    val description: String,
    val address: String,
    val scheduledDate: String,
    val scheduledHour: String,
    val additionalMessage: String?,
    val categories: List<String>,
    val paymentMethod: String,

    // Campo nuevo correctamente separado
    @SerializedName("totalCost")
    val totalCost: Double,

    val status: String
)