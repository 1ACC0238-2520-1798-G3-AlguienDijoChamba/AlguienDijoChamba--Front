package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.local.SessionManager // <-- Importación necesaria
import com.example.alguiendijochamba.data.model.LoginRequestDto // <-- Importación necesaria
import com.example.alguiendijochamba.data.model.RegisterRequestDto
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.domain.usecase.GetReniecInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val dni: String = "",
    val nombres: String = "",
    val apellidos: String = "", // Campo concatenado para mostrar en UI
    val email: String = "",
    val celular: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val metodoPago: String = "Tarjeta",
    val terminosAceptados: Boolean = false,
    val isLoadingReniec: Boolean = false,
    val dniError: String? = null,
    val celularError: String? = null,
    val contrasenaError: String? = null,
    val confirmarContrasenaError: String? = null,
    val terminosError: String? = null,
    // Campos internos para enviar al backend (no se muestran en UI)
    internal val apellidoPaterno: String = "",
    internal val apellidoMaterno: String = ""
)

class RegisterViewModel(
    private val userRepository: UserRepositoryImpl,
    private val getReniecInfoUseCase: GetReniecInfoUseCase,
    private val sessionManager: SessionManager // <-- Recibe SessionManager a través de Koin
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // --- Funciones para actualizar el estado desde la UI ---
    fun onDniChange(dni: String) { if (dni.length <= 8) _uiState.update { it.copy(dni = dni, dniError = null) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }
    fun onCelularChange(celular: String) { if (celular.length <= 9) _uiState.update { it.copy(celular = celular, celularError = null) } }
    fun onContrasenaChange(pass: String) { _uiState.update { it.copy(contrasena = pass, contrasenaError = null) } }
    fun onConfirmarContrasenaChange(pass: String) { _uiState.update { it.copy(confirmarContrasena = pass, confirmarContrasenaError = null) } }
    fun onMetodoPagoChange(metodo: String) { _uiState.update { it.copy(metodoPago = metodo) } }
    fun onTerminosChange(aceptado: Boolean) { _uiState.update { it.copy(terminosAceptados = aceptado, terminosError = null) } }

    // --- Lógica de negocio ---
    fun onDniLookup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReniec = true, dniError = null) }
            val result = getReniecInfoUseCase.execute(_uiState.value.dni)
            result.onSuccess { reniecInfo ->

                val apellidosCompletos = "${reniecInfo.apellidoPaterno} ${reniecInfo.apellidoMaterno}".trim()

                _uiState.update {
                    it.copy(
                        nombres = reniecInfo.nombres,
                        apellidos = apellidosCompletos, // Para mostrar en UI
                        apellidoPaterno = reniecInfo.apellidoPaterno, // Para enviar al backend
                        apellidoMaterno = reniecInfo.apellidoMaterno, // Para enviar al backend
                        isLoadingReniec = false
                    )
                }
            }.onFailure { error ->
                println("❌ Error al consultar RENIEC: ${error.message}")
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
        // Validaciones
        _uiState.update { it.copy(dniError = null, celularError = null, contrasenaError = null, confirmarContrasenaError = null, terminosError = null) }

        val dniValido = _uiState.value.dni.length == 8
        val celularValido = _uiState.value.celular.length == 9
        val contrasenasIguales = _uiState.value.contrasena == _uiState.value.confirmarContrasena
        val contrasenaValida = _uiState.value.contrasena.length >= 8
        val terminosValidos = _uiState.value.terminosAceptados
        val nombresValido = _uiState.value.nombres.isNotBlank()
        val apellidosValido = _uiState.value.apellidoPaterno.isNotBlank() || _uiState.value.apellidoMaterno.isNotBlank()
        val emailValido = _uiState.value.email.isNotBlank()

        if (!dniValido) _uiState.update { it.copy(dniError = "El DNI debe tener 8 dígitos") }
        if (!nombresValido) _uiState.update { it.copy(dniError = "Debe consultar el DNI primero para obtener los nombres") }
        if (!apellidosValido) _uiState.update { it.copy(dniError = "Debe consultar el DNI primero para obtener los apellidos") }
        if (!emailValido) _uiState.update { it.copy(dniError = "El email es requerido") }
        if (!celularValido) _uiState.update { it.copy(celularError = "El celular debe tener 9 dígitos") }
        if (!contrasenaValida) _uiState.update { it.copy(contrasenaError = "Mínimo 8 caracteres") }
        if (!contrasenasIguales) _uiState.update { it.copy(confirmarContrasenaError = "Las contraseñas no coinciden") }
        if (!terminosValidos) _uiState.update { it.copy(terminosError = "Debes aceptar los términos y condiciones") }

        if (dniValido && nombresValido && apellidosValido && emailValido && celularValido && contrasenasIguales && contrasenaValida && terminosValidos) {
            viewModelScope.launch {

                val apellidoPaternoValue = _uiState.value.apellidoPaterno.trim()
                val apellidoMaternoValue = _uiState.value.apellidoMaterno.trim()
                val apellidosCompletos = "$apellidoPaternoValue $apellidoMaternoValue".trim()

                val request = RegisterRequestDto(
                    email = _uiState.value.email.trim(),
                    password = _uiState.value.contrasena,
                    dni = _uiState.value.dni.trim(),
                    nombres = _uiState.value.nombres.trim(),
                    apellidos = apellidosCompletos,
                    celular = _uiState.value.celular.trim()
                )

                // 1. Llamada de Registro
                val result = userRepository.registerUser(request)

                result.onSuccess { userId ->
                    println("✅ Registro exitoso! ID de usuario: $userId. Iniciando sesión para obtener token...")

                    // 2. Ejecuta login automáticamente después del registro exitoso
                    val loginRequest = LoginRequestDto(
                        email = request.email,
                        password = request.password
                    )

                    val loginResult = userRepository.loginUser(loginRequest)

                    loginResult.onSuccess { token ->
                        sessionManager.saveAuthToken(token) // 3. Guarda el token en SessionManager
                        println("✅ Login automático exitoso. Token guardado.")
                        onSuccess() // Navega a CompleteProfileScreen
                    }.onFailure { loginError ->
                        println("❌ Error en login automático: ${loginError.message}")
                        _uiState.update { it.copy(dniError = "Error en login automático después del registro.") }
                    }

                }.onFailure { error ->
                    println("❌ Error en el registro: ${error.message}")
                    _uiState.update { it.copy(dniError = "Error en el registro: ${error.message}") }
                }
            }
        }
    }
}