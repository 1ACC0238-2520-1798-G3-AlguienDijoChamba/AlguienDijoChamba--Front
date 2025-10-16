package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.domain.usecase.GetReniecInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val dni: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = "",
    val celular: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val metodoPago: String = "Tarjeta", // "Tarjeta" o "Billetera"
    val terminosAceptados: Boolean = false,
    val isLoadingReniec: Boolean = false,
    // Errores de validación
    val dniError: String? = null,
    val celularError: String? = null,
    val contrasenaError: String? = null,
    val confirmarContrasenaError: String? = null,
    val terminosError: String? = null
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // Inyectarías el UseCase real con Hilt o Koin
    private val getReniecInfoUseCase = GetReniecInfoUseCase()

    // --- Funciones para actualizar el estado desde la UI ---
    fun onDniChange(dni: String) {
        if (dni.length <= 8) _uiState.update { it.copy(dni = dni, dniError = null) }
    }
    fun onNombresChange(nombres: String) { _uiState.update { it.copy(nombres = nombres) } }
    fun onApellidosChange(apellidos: String) { _uiState.update { it.copy(apellidos = apellidos) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }
    fun onCelularChange(celular: String) {
        if (celular.length <= 9) _uiState.update { it.copy(celular = celular, celularError = null) }
    }
    fun onContrasenaChange(pass: String) { _uiState.update { it.copy(contrasena = pass, contrasenaError = null) } }
    fun onConfirmarContrasenaChange(pass: String) { _uiState.update { it.copy(confirmarContrasena = pass, confirmarContrasenaError = null) } }
    fun onMetodoPagoChange(metodo: String) { _uiState.update { it.copy(metodoPago = metodo) } }
    fun onTerminosChange(aceptado: Boolean) { _uiState.update { it.copy(terminosAceptados = aceptado, terminosError = null) }
    }
    // --- Lógica de negocio ---
    fun onDniLookup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReniec = true, dniError = null) }
            val result = getReniecInfoUseCase.execute(_uiState.value.dni)
            result.onSuccess { reniecInfo ->
                _uiState.update {
                    it.copy(
                        nombres = reniecInfo.nombres,
                        apellidos = reniecInfo.apellidos,
                        isLoadingReniec = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        dniError = error.message ?: "Error al consultar DNI",
                        isLoadingReniec = false
                    )
                }
            }
        }
    }

    fun onCreateAccountClicked(onSuccess: () -> Unit) {
        // Reiniciar errores
        _uiState.update { it.copy(dniError = null, celularError = null, contrasenaError = null, confirmarContrasenaError = null) }

        // Validaciones
        val dniValido = _uiState.value.dni.length == 8
        val celularValido = _uiState.value.celular.length == 9
        val contrasenasIguales = _uiState.value.contrasena == _uiState.value.confirmarContrasena
        val contrasenaValida = _uiState.value.contrasena.length >= 8
        val terminosValidos = _uiState.value.terminosAceptados

        if (!dniValido) _uiState.update { it.copy(dniError = "El DNI debe tener 8 dígitos") }
        if (!celularValido) _uiState.update { it.copy(celularError = "El celular debe tener 9 dígitos") }
        if (!contrasenaValida) _uiState.update { it.copy(contrasenaError = "Mínimo 8 caracteres") }
        if (!contrasenasIguales) _uiState.update { it.copy(confirmarContrasenaError = "Las contraseñas no coinciden") }
        if (!terminosValidos) _uiState.update { it.copy(terminosError = "Debes aceptar los términos y condiciones") }

        if (dniValido && celularValido && contrasenasIguales && contrasenaValida && _uiState.value.terminosAceptados) {
            println("¡Registro exitoso! Creando cuenta...")
            // Aquí llamarías al UseCase para registrar al usuario en tu backend
        }
        if (dniValido && celularValido && contrasenasIguales && contrasenaValida && terminosValidos) {
            println("¡Registro exitoso! Navegando a completar perfil...")
            onSuccess()
        }
    }
}