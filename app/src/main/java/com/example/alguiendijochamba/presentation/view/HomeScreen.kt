package com.example.alguiendijochamba.presentation.view

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alguiendijochamba.domain.model.JobRequest
import com.example.alguiendijochamba.presentation.viewmodel.HomeUiState
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModelFactory
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // --- ✅ CORRECCIÓN AQUÍ: Usamos la Factory ---
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        item { WelcomeHeader(uiState) }
        item {
            val tabs = listOf("Solicitudes", "Saldo", "Ganancias")
            TabRow(selectedTabIndex = uiState.selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }
        }
        item {
            when (uiState.selectedTab) {
                0 -> RequestsTabContent(uiState.newRequests, viewModel)
                1 -> BalanceTabContent()
                2 -> EarningsTabContent()
            }
        }
    }
}

@Composable
fun WelcomeHeader(uiState: HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bienvenido de nuevo,", color = Color.White.copy(alpha = 0.8f))
                    Text(uiState.userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    BadgedBox(badge = { if (uiState.messageCount > 0) Badge { Text("${uiState.messageCount}") } }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Mensajes", tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    BadgedBox(badge = { if (uiState.notificationCount > 0) Badge { Text("${uiState.notificationCount}") } }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.professionalLevel, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Trabajos completados: ${uiState.completedJobs}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                            Text(" ${uiState.starRating}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text("Saldo disponible: S/${"%.2f".format(uiState.availableBalance)}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RequestsTabContent(requests: List<JobRequest>, viewModel: HomeViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuevas Solicitudes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        if (requests.isEmpty()) {
            Text("No hay nuevas solicitudes por el momento.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                requests.forEach { request ->
                    JobRequestCard(
                        request = request,
                        onAccept = { viewModel.acceptRequest(request) },
                        onDecline = { viewModel.declineRequest(request) }
                    )
                }
            }
        }
    }
}

@Composable
fun JobRequestCard(request: JobRequest, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(request.clientName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (request.isUrgent) Badge(containerColor = Color.Red.copy(alpha = 0.1f)) { Text("Urgente", color = Color.Red) }
                if (request.isPending) Badge { Text("Pendiente") }
            }
            Text(request.specialty, color = PrimaryBlue, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(request.location, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(request.dateTime.toFormattedString(), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(request.description)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("S/${"%.2f".format(request.price)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Row {
                    OutlinedButton(onClick = onDecline) { Text("Rechazar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onAccept) { Text("Aceptar") }
                }
            }
        }
    }
}

// Marcadores de posición para las otras pestañas
@Composable fun BalanceTabContent() { Text("Contenido de Saldo", modifier = Modifier.padding(16.dp)) }
@Composable fun EarningsTabContent() { Text("Contenido de Ganancias", modifier = Modifier.padding(16.dp)) }

// Función de extensión para formatear la fecha
private fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("EEE, d MMM 'a las' hh:mm a", Locale("es", "ES"))
    return sdf.format(this)
}