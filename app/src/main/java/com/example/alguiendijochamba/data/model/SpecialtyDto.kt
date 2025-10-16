package com.example.alguiendijochamba.data.model

import com.google.gson.annotations.SerializedName

data class SpecialtyDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)