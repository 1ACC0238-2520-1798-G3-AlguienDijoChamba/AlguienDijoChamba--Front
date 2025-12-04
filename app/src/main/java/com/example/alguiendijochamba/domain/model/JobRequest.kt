package com.example.alguiendijochamba.domain.model

import com.google.gson.annotations.SerializedName

data class JobRequest(
    @SerializedName("id", alternate = ["Id"])
    val id: String,

    @SerializedName("clientId", alternate = ["ClientId"])
    val clientId: String,

    @SerializedName("professionalId", alternate = ["ProfessionalId"])
    val professionalId: String,

    @SerializedName("specialty", alternate = ["Specialty"])
    val specialty: String,

    @SerializedName("description", alternate = ["Description"])
    val description: String,

    @SerializedName("address", alternate = ["Address"])
    val address: String,

    @SerializedName("scheduledDate", alternate = ["ScheduledDate"])
    val scheduledDate: String,

    @SerializedName("scheduledHour", alternate = ["ScheduledHour"])
    val scheduledHour: String,

    // --- CAMPOS QUE TE FALTABAN ---
    @SerializedName("additionalMessage", alternate = ["AdditionalMessage"])
    val additionalMessage: String?,

    @SerializedName("categories", alternate = ["Categories"])
    val categories: List<String>,

    @SerializedName("paymentMethod", alternate = ["PaymentMethod"])
    val paymentMethod: String,

    @SerializedName("totalCost", alternate = ["TotalCost"])
    val totalCost: Double,
    // -----------------------------

    @SerializedName("status", alternate = ["Status"])
    val status: String
)