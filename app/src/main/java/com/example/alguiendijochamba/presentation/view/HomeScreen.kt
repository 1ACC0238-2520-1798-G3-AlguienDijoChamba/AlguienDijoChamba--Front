package com.example.alguiendijochamba.presentation.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alguiendijochamba.domain.model.JobRequest
import com.example.alguiendijochamba.presentation.viewmodel.HomeUiState
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2247C0))
    ) {
        item { WelcomeHeader(uiState) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F2F5))
                    .padding(top = 16.dp)
            ) {
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

                when (uiState.selectedTab) {
                    0 -> RequestsTabContent(
                        requests = uiState.newRequests,
                        viewModel = viewModel,
                        // 🟢 CORRECCIÓN: Aquí pasamos la función de navegación requerida
                        onNavigateToDetail = { jobId ->
                            Log.d("HomeScreen", "Navegando a detalle de Job: $jobId")
                            // Descomenta y ajusta tu ruta cuando tengas la pantalla de detalle creada:
                            // navController.navigate("requests_detail/$jobId")
                            // O si usas el BottomBar para ir a la lista general:
                            // navController.navigate(BottomBarScreen.Requests.route)
                        }
                    )
                    1 -> BalanceTabContent(uiState.newRequests)
                    2 -> EarningsTabContent()
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(uiState: HomeUiState) {
    val messageBadge: @Composable (BoxScope.() -> Unit)? = remember(uiState.messageCount) {
        if (uiState.messageCount > 0) {
            { Badge { Text("${uiState.messageCount}") } }
        } else null
    }

    val notificationBadge: @Composable (BoxScope.() -> Unit)? = remember(uiState.notificationCount) {
        if (uiState.notificationCount > 0) {
            { Badge { Text("${uiState.notificationCount}") } }
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue)
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
                    if (messageBadge != null) {
                        BadgedBox(badge = messageBadge) {
                            Icon(Icons.Default.ChatBubble, contentDescription = "Mensajes", tint = Color.White)
                        }
                    } else {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Mensajes", tint = Color.White)
                    }

                    Spacer(Modifier.width(16.dp))

                    if (notificationBadge != null) {
                        BadgedBox(badge = notificationBadge) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                        }
                    } else {
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
fun RequestsTabContent(
    requests: List<JobRequest>,
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Nuevas Solicitudes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        if (requests.isEmpty()) {
            Text("No hay nuevas solicitudes por el momento.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                requests.forEach { request ->
                    JobRequestCard(
                        request = request,
                        onAccept = { viewModel.acceptRequest(request) },
                        onDecline = { viewModel.declineRequest(request) },
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
fun JobRequestCard(request: JobRequest, onAccept: () -> Unit, onDecline: () -> Unit, onNavigateToDetail: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToDetail(request.id)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Cliente (ID: ${request.clientId.take(4)}...)", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                if (request.status == "Pending") {
                    Badge(containerColor = Color.Red.copy(alpha = 0.1f)) { Text("Pendiente", color = Color.Red) }
                } else {
                    Badge { Text(request.status) }
                }
            }
            Text(request.specialty, color = PrimaryBlue, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(request.address, style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${formatDateString(request.scheduledDate)} - ${request.scheduledHour}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(request.description)

            request.additionalMessage?.let {
                Spacer(Modifier.height(4.dp))
                Text("Nota: $it", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            // --- SECCIÓN PRECIO RESALTADO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total a recibir:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "S/${"%.2f".format(request.totalCost)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF2E7D32)
                )
            }
            // ---------------------------------

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDecline) { Text("Rechazar") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onAccept) { Text("Aceptar") }
            }
        }
    }
}

@Composable
fun BalanceTabContent(jobRequests: List<JobRequest>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ledger", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            jobRequests.forEach { request ->
                BalanceLedgerCard(request)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { /* Acción para ir a la pantalla de pagos */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Payments")
        }
    }
}

@Composable
fun BalanceLedgerCard(request: JobRequest) {
    val badgeContent: @Composable (BoxScope.() -> Unit)? = null

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Cliente ${request.clientId.take(4)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                if (badgeContent != null) {
                    BadgedBox(badge = badgeContent) {}
                }
            }

            Text(request.specialty, color = PrimaryBlue, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(request.address, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(formatDateString(request.scheduledDate), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))

            Text(request.description)

            Spacer(Modifier.height(16.dp))

            Divider()
            Spacer(Modifier.height(8.dp))

            val initialPayment = request.totalCost / 2
            val finalPayment = request.totalCost / 2
            val isFinalCompleted = request.status == "Completed"

            PaymentDetailRow(title = "Total Amount:", amount = request.totalCost)
            PaymentDetailRow(
                title = "Initial Payment:",
                amount = initialPayment,
                icon = Icons.AutoMirrored.Filled.Launch,
                iconColor = PrimaryBlue
            )
            PaymentDetailRow(
                title = "Final Payment:",
                amount = finalPayment,
                icon = if (isFinalCompleted) Icons.Default.CheckCircle else null,
                iconColor = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun PaymentDetailRow(
    title: String,
    amount: Double,
    icon: ImageVector? = null,
    iconColor: Color = Color.Transparent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("S/${"%.2f".format(amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconColor)
            }
        }
    }
}

@Composable
fun EarningsTabContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val pendingAmount = 450.0
        val availableAmount = 1250.0
        val totalEarned = 8750.0

        EarningsCard(
            title = "Pending",
            amount = pendingAmount,
            icon = Icons.Default.ChatBubble,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        EarningsCard(
            title = "Available",
            amount = availableAmount,
            icon = Icons.Default.CheckCircle,
            iconColor = MaterialTheme.colorScheme.primary
        )

        EarningsCard(
            title = "Total Earned",
            amount = totalEarned,
            icon = Icons.Default.ArrowUpward,
            iconColor = PrimaryBlue,
            isPainter = false
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { /* Navegar a la pantalla de detalles de pagos */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("View Payment Details")
        }
    }
}

@Composable
fun EarningsCard(
    title: String,
    amount: Double,
    icon: Any?,
    iconColor: Color,
    isPainter: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "S/${"%.2f".format(amount)}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    when {
                        isPainter && icon is androidx.compose.ui.graphics.painter.Painter -> Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                        !isPainter && icon is ImageVector -> Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDateString(isoDate: String): String {
    return try {
        val datePart = isoDate.split("T")[0]
        datePart
    } catch (e: Exception) {
        isoDate
    }
}