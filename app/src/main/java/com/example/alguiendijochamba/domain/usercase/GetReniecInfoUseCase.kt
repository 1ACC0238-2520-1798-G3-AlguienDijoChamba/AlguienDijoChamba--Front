package com.example.alguiendijochamba.domain.usecase

import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.domain.model.ReniecInfo

/**
 * Caso de uso para obtener información de RENIEC basándose en el DNI.
 * Este caso de uso encapsula la lógica de negocio para validar el DNI
 * y obtener la información del ciudadano desde el repositorio.
 */
class GetReniecInfoUseCase(private val repository: UserRepositoryImpl) {
    suspend fun execute(dni: String): Result<ReniecInfo> {
        // Validación de negocio
        if (dni.length != 8) {
            return Result.failure(Exception("El DNI debe tener 8 dígitos."))
        }

        // Llamada al repositorio para obtener datos reales
        return repository.getReniecInfo(dni)
    }
}