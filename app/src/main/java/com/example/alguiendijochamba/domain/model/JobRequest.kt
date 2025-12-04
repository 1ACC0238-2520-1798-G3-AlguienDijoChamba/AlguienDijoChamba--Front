package com.example.alguiendijochamba.domain.model

import java.util.Date

data class JobRequest(
    val id: String, // Guid viene como String
    val clientId: String,
    val professionalId: String,
    val specialty: String,
    val description: String,
    val address: String,
    val scheduledDate: String, // Viene como ISO String
    val scheduledHour: String,
    val additionalMessage: String?,
    val categories: List<String>,
    val paymentMethod: String,
    val totalCost: Double,
    val status: String


)