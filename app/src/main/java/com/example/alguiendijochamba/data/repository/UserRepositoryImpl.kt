package com.example.alguiendijochamba.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.alguiendijochamba.data.model.*
import com.example.alguiendijochamba.data.remote.ApiService
import com.example.alguiendijochamba.domain.model.Profile
import com.example.alguiendijochamba.domain.model.ReniecInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalStateException


// Constructor actualizado para Koin: Recibe ApiService y Context
class UserRepositoryImpl(
    private val apiService: ApiService,
    private val context: Context // Necesario para manejar los URIs de los archivos
) {

    // --- MÉTODOS DE AUTENTICACIÓN Y RENIEC ---

    suspend fun getReniecInfo(dni: String): Result<ReniecInfo> = try {
        println("Consultando RENIEC para DNI: $dni")
        val response = apiService.getReniecInfo(dni)

        println("Respuesta HTTP: ${response.code()}")
        println("   Success: ${response.isSuccessful}")
        println("   Message: ${response.message()}")

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!

            println("Body recibido del backend RENIEC:")
            println("   nombres: '${body.nombres}'")
            println("   apellidos (concatenado): '${body.apellidos}'")
            println("   apellidoPaterno: '${body.apellidoPaterno}'")
            println("   apellidoMaterno: '${body.apellidoMaterno}'")

            val apellidoPaterno: String
            val apellidoMaterno: String

            if (!body.apellidos.isNullOrEmpty()) {
                println("Backend envía apellidos concatenados, dividiéndolos...")
                val apellidosArray = body.apellidos.trim().split("\\s+".toRegex())
                apellidoPaterno = apellidosArray.getOrNull(0) ?: ""
                apellidoMaterno = apellidosArray.drop(1).joinToString(" ").ifEmpty { "" }
            } else {
                println("Backend envía apellidos separados")
                apellidoPaterno = body.apellidoPaterno ?: ""
                apellidoMaterno = body.apellidoMaterno ?: ""
            }

            val reniecInfo = ReniecInfo(
                nombres = body.nombres ?: "",
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno
            )
            Result.success(reniecInfo)
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error en respuesta RENIEC: $errorBody")
            Result.failure(Exception("DNI no encontrado - HTTP ${response.code()}"))
        }
    } catch (e: Exception) {
        println("Excepción al consultar RENIEC: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun registerUser(request: RegisterRequestDto): Result<String> = try {
        println("Enviando registro al backend:")
        println("   Nombres: '${request.nombres}', Apellidos: '${request.apellidos}'")

        val response = apiService.registerUser(request)

        if (response.isSuccessful && response.body() != null) {
            println("Registro exitoso, userId: ${response.body()!!.userId}")
            Result.success(response.body()!!.userId)
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error en el registro: $errorBody")
            Result.failure(Exception("Error en el registro: $errorBody"))
        }
    } catch (e: Exception) {
        println("Excepción en registerUser: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun loginUser(request: LoginRequestDto): Result<String> = try {
        val response = apiService.loginUser(request)
        if (response.isSuccessful && response.body() != null) Result.success(response.body()!!.token)
        else Result.failure(Exception("Credenciales inválidas"))
    } catch (e: Exception) { Result.failure(e) }

    // --- MÉTODOS DEL PERFIL DE USUARIO ---

    suspend fun getMyProfile(): Result<Profile> = try {
        val response = apiService.getMyProfile()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.toDomain())
        } else {
            Result.failure(Exception("Error al cargar perfil: ${response.code()}"))
        }
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateMyProfile(updateRequest: UpdateProfileRequestDto): Result<Unit> = try {
        val response = apiService.updateMyProfile(updateRequest)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error al actualizar perfil: ${response.code()}"))
        }
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteAccount(): Result<Unit> = try {
        val response = apiService.deleteAccount()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error al eliminar cuenta: ${response.code()}"))
        }
    } catch (e: Exception) { Result.failure(e) }


    // --- MÉTODOS DE "COMPLETAR PERFIL" ---

    suspend fun uploadProfilePhoto(photoUri: Uri): Result<String> = try {
        val filePart = createMultipartBodyPart(photoUri, "file")
        val response = apiService.uploadProfilePhoto(filePart)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.fileUrl)
        } else {
            Result.failure(Exception("Error al subir foto: ${response.code()}"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun uploadCertification(certUri: Uri): Result<String> = try {
        val filePart = createMultipartBodyPart(certUri, "file")
        val response = apiService.uploadCertification(filePart)
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!.fileUrl)
        } else {
            Result.failure(Exception("Error al subir certificación: ${response.code()}"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun completeProfile(request: CompleteProfileRequestDto): Result<Unit> = try {
        val response = apiService.completeProfile(request)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error al guardar perfil: ${response.code()}"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }


    // --- FUNCIÓN UTILITARIA CLAVE ---

    /**
     * Convierte un Uri (de imagen o PDF) en un MultipartBody.Part que Retrofit puede enviar.
     */
    private fun createMultipartBodyPart(uri: Uri, partName: String): MultipartBody.Part {
        // 1. Obtén el stream de contenido del Uri
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("No se pudo abrir el InputStream del Uri")

        // 2. Obtén el tipo MIME (ej: "image/jpeg", "application/pdf")
        val mimeType = context.contentResolver.getType(uri)

        // 3. Obtén el nombre del archivo
        var fileName = "unknown"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        // Sanitizar el nombre del archivo para evitar problemas de seguridad
        val sanitizedFileName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")

        // 4. Copia el stream a un archivo temporal (necesario para RequestBody)
        val file = File(context.cacheDir, sanitizedFileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        // 5. Crea el RequestBody desde el archivo temporal
        val requestBody = file.asRequestBody(mimeType?.toMediaTypeOrNull())

        // Limpia el caché para que no se acumulen archivos
        file.deleteOnExit()

        // 6. Crea y devuelve el MultipartBody.Part
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }
}


/**
 * Función de extensión que convierte el DTO de la Red (ProfileResponseDto)
 * al Modelo de Dominio "limpio" (Profile) que usa la UI.
 */
private fun ProfileResponseDto.toDomain(): Profile {
    return Profile(
        userName = this.userName,
        professionalLevel = this.professionalLevel,
        starRating = this.starRating,
        completedJobs = this.completedJobs,
        availableBalance = this.availableBalance,

        nombres = this.nombres,
        apellidos = this.apellidos,
        ocupacion = this.ocupacion,
        email = this.email,
        celular = this.celular,
        fechaNacimiento = this.fechaNacimiento,
        genero = this.genero,
        fotoPerfilUrl = this.fotoPerfilUrl
    )
}