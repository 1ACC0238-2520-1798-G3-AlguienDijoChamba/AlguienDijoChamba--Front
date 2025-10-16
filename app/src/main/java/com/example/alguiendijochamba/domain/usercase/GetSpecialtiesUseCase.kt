package com.example.alguiendijochamba.domain.usecase

import com.example.alguiendijochamba.domain.model.Specialty
// import com.example.alguiendijochamba.domain.repository.SpecialtyRepository

// Nota: Para que funcione, necesitarás crear una interfaz SpecialtyRepository
// en 'domain' y su implementación en 'data' que llame a la ApiService.
class GetSpecialtiesUseCase(/*private val repository: SpecialtyRepository*/) {

    suspend fun execute(): Result<List<Specialty>> {
        // En la implementación real, llamarías al repositorio:
        // return repository.getSpecialties()

        // --- Simulación para pruebas ---
        return try {
            val specialties = listOf(
                Specialty(1, "Gasfitería"),
                Specialty(2, "Electricidad"),
                Specialty(3, "Carpintería"),
                Specialty(4, "Pintura"),
                Specialty(5, "Albañilería"),
                Specialty(6, "Jardinería")
            )
            Result.success(specialties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}