// domain/model/JobRequest.kt
package com.example.alguiendijochamba.domain.model

import java.util.Date

data class JobRequest(
    val id: Int,
    val clientName: String,
    val specialty: String,
    val location: String,
    val dateTime: Date,
    val description: String,
    val price: Double,
    val isUrgent: Boolean = false,
    val isPending: Boolean = false,

    // --- CAMPOS DE BALANCE AÑADIDOS ---
    val totalAmount: Double,
    val initialPayment: Double,
    val finalPayment: Double,
    val isFinalPaymentCompleted: Boolean = false
)