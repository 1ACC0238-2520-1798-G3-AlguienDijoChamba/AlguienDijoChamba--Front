package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alguiendijochamba.domain.model.JobRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

data class HomeUiState(
    val userName: String = "Carlos Rodriguez", // Se obtendrá del backend
    val professionalLevel: String = "Gold Professional",
    val starRating: Double = 4.9,
    val completedJobs: Int = 127,
    val availableBalance: Double = 1250.0,
    val selectedTab: Int = 0, // 0: Requests, 1: Balance, 2: Earnings
    val newRequests: List<JobRequest> = emptyList(),
    val messageCount: Int = 3,
    val notificationCount: Int = 3
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNewRequests()
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