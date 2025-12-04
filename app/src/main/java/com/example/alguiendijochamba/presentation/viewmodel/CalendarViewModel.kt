package com.example.alguiendijochamba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alguiendijochamba.data.model.JobRequestDto
import com.example.alguiendijochamba.data.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarUiState(
    val jobs: List<JobRequestDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalendarViewModel(private val repository: UserRepositoryImpl) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadScheduledJobs()
    }

    fun loadScheduledJobs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getScheduledJobs()
                .onSuccess { jobs ->
                    _uiState.update { it.copy(jobs = jobs, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}