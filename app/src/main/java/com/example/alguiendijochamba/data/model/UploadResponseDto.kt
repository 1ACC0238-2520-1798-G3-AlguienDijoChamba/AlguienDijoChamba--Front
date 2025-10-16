package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class UploadResponseDto(
    @SerializedName("fileUrl")
    val fileUrl: String
)