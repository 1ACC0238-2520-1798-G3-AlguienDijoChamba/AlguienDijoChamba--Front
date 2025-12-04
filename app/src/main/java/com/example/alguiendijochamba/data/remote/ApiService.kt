package com.example.alguiendijochamba.data.remote

import com.example.alguiendijochamba.data.model.CompleteProfileRequestDto // <-- NUEVO
import com.example.alguiendijochamba.data.model.JobRequestDto
import com.example.alguiendijochamba.data.model.UploadResponseDto // <-- NUEVO
import com.example.alguiendijochamba.data.model.LoginRequestDto
import com.example.alguiendijochamba.data.model.LoginResponseDto
import com.example.alguiendijochamba.data.model.ProfileResponseDto
import com.example.alguiendijochamba.data.model.RegisterRequestDto
import com.example.alguiendijochamba.data.model.RegisterResponseDto
import com.example.alguiendijochamba.data.model.ReniecResponseDto
import com.example.alguiendijochamba.data.model.UpdateProfileRequestDto
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody

interface ApiService {
    @GET("api/v1/professionals/reniec/{dni}")
    suspend fun getReniecInfo(@Path("dni") dni: String): Response<ReniecResponseDto>

    @POST("api/v1/iam/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("api/v1/iam/login")
    suspend fun loginUser(@Body loginRequest: LoginRequestDto): Response<LoginResponseDto>

    @GET("api/v1/professionals/my-profile")
    suspend fun getMyProfile(): Response<ProfileResponseDto>

    @GET("api/v1/jobs/scheduled")
    suspend fun getScheduledJobs(): Response<List<JobRequestDto>>

    // Actualizar perfil del usuario
    @PUT("api/v1/professionals/my-profile")
    suspend fun updateMyProfile(@Body updateRequest: UpdateProfileRequestDto): Response<Unit>

    // Eliminar cuenta del usuario
    @DELETE("api/v1/iam/delete-account")
    suspend fun deleteAccount(): Response<Unit>
    // 1. Para subir la foto de perfil
    @Multipart
    @POST("api/v1/professionals/upload-photo")
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part
    ): Response<UploadResponseDto> // Reusa el DTO que ya tenías

    // 2. Para subir una certificación
    @Multipart
    @POST("api/v1/professionals/upload-certification")
    suspend fun uploadCertification(
        @Part file: MultipartBody.Part
    ): Response<UploadResponseDto>

    // 3. Para guardar los datos del perfil
    @POST("api/v1/professionals/complete-profile")
    suspend fun completeProfile(
        @Body request: CompleteProfileRequestDto
    ): Response<Unit> // O un DTO de respuesta si el backend lo envía
}