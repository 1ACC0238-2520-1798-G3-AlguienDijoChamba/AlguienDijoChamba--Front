package com.example.alguiendijochamba.data.remote

import com.example.alguiendijochamba.data.model.LoginRequestDto
import com.example.alguiendijochamba.data.model.LoginResponseDto
import com.example.alguiendijochamba.data.model.ProfileResponseDto
import com.example.alguiendijochamba.data.model.RegisterRequestDto
import com.example.alguiendijochamba.data.model.RegisterResponseDto
import com.example.alguiendijochamba.data.model.ReniecResponseDto
import com.example.alguiendijochamba.data.model.UpdateProfileRequestDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/v1/professionals/reniec/{dni}")
    suspend fun getReniecInfo(@Path("dni") dni: String): Response<ReniecResponseDto>

    @POST("api/v1/iam/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("api/v1/iam/login")
    suspend fun loginUser(@Body loginRequest: LoginRequestDto): Response<LoginResponseDto>

    @GET("api/v1/professionals/my-profile")
    suspend fun getMyProfile(): Response<ProfileResponseDto>

    // Actualizar perfil del usuario
    @PUT("api/v1/professionals/my-profile")
    suspend fun updateMyProfile(@Body updateRequest: UpdateProfileRequestDto): Response<Unit>

    // Eliminar cuenta del usuario
    @DELETE("api/v1/iam/delete-account")
    suspend fun deleteAccount(): Response<Unit>
}