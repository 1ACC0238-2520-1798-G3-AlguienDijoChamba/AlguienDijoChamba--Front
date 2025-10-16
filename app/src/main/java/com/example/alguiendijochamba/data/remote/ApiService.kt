package com.example.alguiendijochamba.data.remote

import com.example.alguiendijochamba.data.model.ReniecResponseDto
import com.example.alguiendijochamba.data.model.SpecialtyDto
import com.example.alguiendijochamba.data.model.UploadResponseDto
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // Aquí podrías tener otras llamadas como login, etc.

    @GET("api/reniec/{dni}") // La ruta en tu backend para la consulta
    suspend fun getReniecInfo(@Path("dni") dni: String): ReniecResponseDto

    @GET("api/specialties") // Ruta en tu backend para obtener la lista de especialidades
    suspend fun getSpecialties(): List<SpecialtyDto>

    @Multipart // Indica que esta llamada enviará archivos
    @POST("api/upload/profile-photo") // Ruta para subir la foto de perfil
    suspend fun uploadProfilePhoto(@Part image: MultipartBody.Part): UploadResponseDto
}