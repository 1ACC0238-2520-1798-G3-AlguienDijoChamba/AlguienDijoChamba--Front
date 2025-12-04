package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class JobRequestDto(
    @SerializedName("id") val id: String,
    @SerializedName("specialty") val specialty: String,
    @SerializedName("description") val description: String,
    @SerializedName("address") val address: String,
    @SerializedName("scheduledDate") val scheduledDate: Date, // Asegúrate de que Gson maneje fechas o usa String
    @SerializedName("scheduledHour") val scheduledHour: String,
    @SerializedName("totalCost") val totalCost: Double,
    @SerializedName("status") val status: String
)