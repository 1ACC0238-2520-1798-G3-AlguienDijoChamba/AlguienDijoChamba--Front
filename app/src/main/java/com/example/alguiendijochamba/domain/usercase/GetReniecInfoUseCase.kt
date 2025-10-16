package com.example.alguiendijochamba.domain.usecase

import com.example.alguiendijochamba.domain.model.ReniecInfo
// Asume que tienes un UserRepository que puede obtener estos datos.
// import com.example.alguiendijochamba.domain.repository.UserRepository

// Nota: Necesitarás crear un UserRepository en la capa de dominio y su implementación
// en la capa de datos para que esto funcione. Por simplicidad, lo omitimos aquí,
// pero el ViewModel llamará a este UseCase.
class GetReniecInfoUseCase(/*private val repository: UserRepository*/) {
    suspend fun execute(dni: String): Result<ReniecInfo> {
        if (dni.length != 8) {
            return Result.failure(Exception("El DNI debe tener 8 dígitos."))
        }
        // Aquí llamarías al repositorio, que a su vez llama a la ApiService.
        // return repository.getReniecInfo(dni)

        // --- Simulación para pruebas ---
        return if (dni == "12345678") {
            Result.success(ReniecInfo(nombres = "Juan Alberto", apellidos = "Pérez Gómez"))
        } else {
            Result.failure(Exception("DNI no encontrado."))
        }
    }
}