package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class CompleteProfileRequestDto(
    @SerializedName("yearsOfExperience") val yearsOfExperience: Int,
    @SerializedName("hourlyRate") val hourlyRate: Double?,
    @SerializedName("professionalBio") val professionalBio: String,
    @SerializedName("profilePhotoUrl") val profilePhotoUrl: String?,
    @SerializedName("certificationUrls") val certificationUrls: List<String>?
)