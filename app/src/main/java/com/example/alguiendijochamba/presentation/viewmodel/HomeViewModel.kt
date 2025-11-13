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
import com.example.alguiendijochamba.data.remote.SignalRService
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
    private val repository: UserRepositoryImpl,
    private val signalRService: SignalRService // <-- Inyectamos SignalR
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
        initializeSignalR()
    }

    private fun initializeSignalR() {
        // Iniciar conexión en hilo secundario
        viewModelScope.launch {
            try {
                signalRService.startConnection()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Escuchar nuevas solicitudes que llegan por el socket
            signalRService.jobRequests.collect { newJob ->
                val currentList = _uiState.value.newRequests.toMutableList()
                // Evitar duplicados si es necesario
                if (currentList.none { it.id == newJob.id }) {
                    currentList.add(0, newJob) // Agregar al inicio
                    _uiState.update { it.copy(newRequests = currentList) }
                }
            }
        }
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
        // Simulación de datos del backend con el NUEVO modelo
        // Nota: Los campos 'id' ahora son String (GUID simulados)
        val requests = listOf(
            JobRequest(
                id = "job-guid-1",
                clientId = "client-1",
                professionalId = "prof-1",
                specialty = "Plumbing",
                description = "Kitchen sink leak needs urgent repair",
                address = "San Isidro, Lima",
                scheduledDate = "2025-11-15T10:00:00", // Formato ISO String
                scheduledHour = "10:00 AM",
                additionalMessage = "Please bring tools",
                categories = listOf("Repair", "Urgent"),
                paymentMethod = "Cash",
                totalCost = 120.0,
                status = "Pending"
            ),
            JobRequest(
                id = "job-guid-2",
                clientId = "client-2",
                professionalId = "prof-1",
                specialty = "Electrical",
                description = "Install new ceiling fan",
                address = "Miraflores, Lima",
                scheduledDate = "2025-11-16T14:30:00",
                scheduledHour = "02:30 PM",
                additionalMessage = null,
                categories = listOf("Installation"),
                paymentMethod = "Credit Card",
                totalCost = 200.0,
                status = "Pending"
            )
        )
        _uiState.update { it.copy(newRequests = requests) }
    }
    fun acceptRequest(request: JobRequest) {
        viewModelScope.launch {
            // 1. Llamar a SignalR para aceptar
            // Nota: Usamos el totalCost original como propuesta inicial,
            // o podrías abrir un Dialog para cambiar el precio.
            signalRService.respondToRequest(
                jobId = request.id,
                accepted = true,
                proposedCost = request.totalCost
            )

            // 2. Actualizar UI localmente (remover de la lista de pendientes)
            removeRequestFromList(request.id)
        }
    }

    fun declineRequest(request: JobRequest) {
        viewModelScope.launch {
            signalRService.respondToRequest(
                jobId = request.id,
                accepted = false,
                proposedCost = 0.0
            )
            removeRequestFromList(request.id)
        }
    }

    private fun removeRequestFromList(jobId: String) {
        val updatedList = _uiState.value.newRequests.filterNot { it.id == jobId }
        _uiState.update { it.copy(newRequests = updatedList) }
    }

    override fun onCleared() {
        super.onCleared()
        signalRService.stopConnection()
    }
}