package com.example.alguiendijochamba.presentation.viewmodel

// 1. Quita la importación de Application
import androidx.lifecycle.ViewModel // 2. Cambia de AndroidViewModel a ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import com.example.alguiendijochamba.domain.model.JobRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class HomeUiState(
    val userName: String = "Cargando...",
    val professionalLevel: String = "Gold Professional",
    val starRating: Double = 4.9,
    val completedJobs: Int = 127,
    val availableBalance: Double = 1250.0,
    val selectedTab: Int = 0, // 0: Requests, 1: Balance, 2: Earnings
    val newRequests: List<JobRequest> = emptyList(),
    val messageCount: Int = 3,
    val notificationCount: Int = 3,
    val isLoading: Boolean = false,
    val error: String? = null
)

// 3. Ya no es AndroidViewModel y recibe las dependencias en el constructor
class HomeViewModel(
    private val repository: UserRepositoryImpl
) : ViewModel() {

    // 4. ¡Esta línea se elimina! Koin la provee.
    // private val repository = UserRepositoryImpl(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNewRequests()
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getMyProfile()
                .onSuccess { profile ->

                    val fullName = profile.nombres.trim()

                    _uiState.update {
                        it.copy(
                            userName = fullName, // Usará solo el nombre (ej: "LUIS ALBERTO")
                            professionalLevel = profile.professionalLevel,
                            starRating = profile.starRating,
                            completedJobs = profile.completedJobs,
                            availableBalance = profile.availableBalance,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido",
                            // Muestra el mensaje de error o mantén "Error al cargar"
                            userName = "Error al cargar"
                        )
                    }
                }
        }
    }

    fun onTabSelected(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    private fun loadNewRequests() {
        // Simulación de datos del backend
        val requests = listOf(
            JobRequest(1, "María González", "Plumbing", "San Isidro, Lima", Date(), "Kitchen sink leak needs urgent repair", 120.0, isUrgent = true),
            JobRequest(2, "Roberto Silva", "Electrical", "Miraflores, Lima", Date(), "Install new ceiling fan in living room", 200.0, isPending = true),
            JobRequest(3, "Ana Torres", "Carpentry", "Surco, Lima", Date(), "Custom bookshelf installation", 350.0)
        )
        _uiState.update { it.copy(newRequests = requests) }
    }

    fun acceptRequest(request: JobRequest) {
        println("Request accepted: ${request.clientName}")
        // Lógica para aceptar y eliminar de la lista de "nuevos"
    }

    fun declineRequest(request: JobRequest) {
        println("Request declined: ${request.clientName}")
        // Lógica para declinar
    }
}