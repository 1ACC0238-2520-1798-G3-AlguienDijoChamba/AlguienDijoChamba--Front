package com.example.alguiendijochamba.data.repository

import android.content.Context
import com.example.alguiendijochamba.data.di.NetworkModule
import com.example.alguiendijochamba.data.model.*
import com.example.alguiendijochamba.data.remote.ApiService
import com.example.alguiendijochamba.domain.model.Profile
import com.example.alguiendijochamba.domain.model.ReniecInfo

class UserRepositoryImpl(context: Context) {

    // Se inicializa el servicio pasando el contexto
    private val apiService: ApiService = NetworkModule.provideApiService(context.applicationContext)

    suspend fun getReniecInfo(dni: String): Result<ReniecInfo> = try {
        println("Consultando RENIEC para DNI: $dni")
        println("   URL: ${NetworkModule.BASE_URL}api/v1/professionals/reniec/$dni")

        val response = apiService.getReniecInfo(dni)

        println("Respuesta HTTP: ${response.code()}")
        println("   Success: ${response.isSuccessful}")
        println("   Message: ${response.message()}")

        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!

            // Logs para depuración
            println("Body recibido del backend RENIEC:")
            println("   nombres: '${body.nombres}' (length: ${body.nombres?.length ?: 0})")
            println("   apellidos (concatenado): '${body.apellidos}' (length: ${body.apellidos?.length ?: 0})")
            println("   apellidoPaterno: '${body.apellidoPaterno}' (length: ${body.apellidoPaterno?.length ?: 0})")
            println("   apellidoMaterno: '${body.apellidoMaterno}' (length: ${body.apellidoMaterno?.length ?: 0})")

            // Determinar apellidos: usar el campo que venga del backend
            val apellidoPaterno: String
            val apellidoMaterno: String

            if (!body.apellidos.isNullOrEmpty()) {
                // El backend envía apellidos concatenados, los dividimos
                println("Backend envía apellidos concatenados, dividiéndolos...")
                val apellidosArray = body.apellidos.trim().split("\\s+".toRegex())
                apellidoPaterno = apellidosArray.getOrNull(0) ?: ""
                apellidoMaterno = apellidosArray.drop(1).joinToString(" ").ifEmpty { "" }
                println("   → Paterno: '$apellidoPaterno'")
                println("   → Materno: '$apellidoMaterno'")
            } else {
                // El backend envía apellidos separados
                println("Backend envía apellidos separados")
                apellidoPaterno = body.apellidoPaterno ?: ""
                apellidoMaterno = body.apellidoMaterno ?: ""
            }

            val reniecInfo = ReniecInfo(
                nombres = body.nombres ?: "",
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno
            )

            println("ReniecInfo creado:")
            println("   nombres: '${reniecInfo.nombres}'")
            println("   apellidoPaterno: '${reniecInfo.apellidoPaterno}'")
            println("   apellidoMaterno: '${reniecInfo.apellidoMaterno}'")

            Result.success(reniecInfo)
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error en respuesta RENIEC:")
            println("   Code: ${response.code()}")
            println("   Message: ${response.message()}")
            println("   Error Body: $errorBody")
            Result.failure(Exception("DNI no encontrado - HTTP ${response.code()}"))
        }
    } catch (e: Exception) {
        println("Excepción al consultar RENIEC:")
        println("   Tipo: ${e.javaClass.simpleName}")
        println("   Mensaje: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun registerUser(request: RegisterRequestDto): Result<String> = try {
        println("RegisterUser llamado con:")
        println("   Email: '${request.email}' (length: ${request.email.length}, isEmpty: ${request.email.isEmpty()})")
        println("   Password: '${if (request.password.isNotEmpty()) "***" else "VACÍO"}' (length: ${request.password.length}, isEmpty: ${request.password.isEmpty()})")
        println("   DNI: '${request.dni}' (length: ${request.dni.length}, isEmpty: ${request.dni.isEmpty()})")
        println("   Nombres: '${request.nombres}' (length: ${request.nombres.length}, isEmpty: ${request.nombres.isEmpty()})")
        println("   Apellidos: '${request.apellidos}' (length: ${request.apellidos.length}, isEmpty: ${request.apellidos.isEmpty()})")
        println("   Celular: '${request.celular}' (length: ${request.celular.length}, isEmpty: ${request.celular.isEmpty()})")

        val response = apiService.registerUser(request)

        println("Respuesta del servidor:")
        println("   isSuccessful: ${response.isSuccessful}")
        println("   code: ${response.code()}")
        println("   message: ${response.message()}")

        if (response.isSuccessful && response.body() != null) {
            println("Registro exitoso, userId: ${response.body()!!.userId}")
            Result.success(response.body()!!.userId)
        } else {
            val errorBody = response.errorBody()?.string()
            println("Error en el registro:")
            println("   errorBody: $errorBody")
            Result.failure(Exception("Error en el registro: $errorBody"))
        }
    } catch (e: Exception) {
        println("Excepción en registerUser:")
        println("   Tipo: ${e.javaClass.simpleName}")
        println("   Mensaje: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun loginUser(request: LoginRequestDto): Result<String> = try {
        val response = apiService.loginUser(request)
        if (response.isSuccessful && response.body() != null) Result.success(response.body()!!.token)
        else Result.failure(Exception("Credenciales inválidas"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getMyProfile(): Result<Profile> = try {
        val response = apiService.getMyProfile()
        if (response.isSuccessful && response.body() != null) {
            // response.body()!! es ProfileResponseDto
            // .toDomain() lo convierte en Profile
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

        // --- ESTAS LÍNEAS YA NO DARÁN ERROR ---
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