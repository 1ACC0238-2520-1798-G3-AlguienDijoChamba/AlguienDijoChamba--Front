// presentation/view/RequestsScreen.kt
package com.example.alguiendijochamba.presentation.view

import android.util.Log // 🟢 Importación necesaria para el Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    navController: NavController,
    // Reutilizamos HomeViewModel ya que contiene la lógica de las solicitudes
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Título claro de la sección
                    Text(
                        text = "Solicitudes Recibidas",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->

        // El componente RequestsTabContent que ya tienes en HomeScreen
        // contiene la lógica de mostrar la lista de tarjetas JobRequest.
        RequestsTabContent(
            requests = uiState.newRequests,
            viewModel = viewModel,
            // 🟢 CORRECCIÓN: Agregamos el parámetro obligatorio de navegación
            onNavigateToDetail = { jobId ->
                Log.d("RequestsScreen", "Click en solicitud ID: $jobId")
                // Aquí pondrás la navegación real cuando crees la pantalla de detalle
                // navController.navigate("request_detail/$jobId")
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F2F5)) // Fondo gris claro como en la imagen
        )
    }
}