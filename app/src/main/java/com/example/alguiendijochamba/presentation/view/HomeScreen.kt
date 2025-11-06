// presentation/view/HomeScreen.kt
package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.domain.model.JobRequest
import com.example.alguiendijochamba.presentation.viewmodel.HomeUiState
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
                    0 -> RequestsTabContent(uiState.newRequests, viewModel)
                    1 -> BalanceTabContent(uiState.newRequests)
                    2 -> EarningsTabContent()
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(uiState: HomeUiState) {
    // Definimos las funciones Composable del Badge para que el compilador no se queje
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
                    // USO DEL CONDICIONAL IF EXTERNO para evitar errores de nullabilidad
                    if (messageBadge != null) {
                        BadgedBox(badge = messageBadge) {
                            Icon(Icons.Default.ChatBubble, contentDescription = "Mensajes", tint = Color.White)
                        }
                    } else {
                        // Mostrar sin badge si es 0
                        Icon(Icons.Default.ChatBubble, contentDescription = "Mensajes", tint = Color.White)
                    }

                    Spacer(Modifier.width(16.dp))

                    // USO DEL CONDICIONAL IF EXTERNO para evitar errores de nullabilidad
                    if (notificationBadge != null) {
                        BadgedBox(badge = notificationBadge) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                        }
                    } else {
                        // Mostrar sin badge si es 0
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
    // Definición explícita y nullable del Badge
    val badgeContent: @Composable (BoxScope.() -> Unit)? = remember(request.id) {
        if (request.id == 1) {
            { Badge { Text("2") } }
        } else null
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(request.clientName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // USO DEL CONDICIONAL IF EXTERNO para evitar errores de nullabilidad
                if (badgeContent != null) {
                    BadgedBox(badge = badgeContent) {}
                }
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

            Divider()
            Spacer(Modifier.height(8.dp))

            PaymentDetailRow(title = "Total Amount:", amount = request.totalAmount)
            PaymentDetailRow(
                title = "Initial Payment:",
                amount = request.initialPayment,
                icon = Icons.AutoMirrored.Filled.Launch,
                iconColor = PrimaryBlue
            )
            PaymentDetailRow(
                title = "Final Payment:",
                amount = request.finalPayment,
                icon = if (request.isFinalPaymentCompleted) Icons.Default.CheckCircle else null,
                iconColor = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun PaymentDetailRow(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
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

// --- IMPLEMENTACIÓN FINAL DE EARNINGS ---

@Composable
fun EarningsTabContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Datos de prueba
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
    icon: Any?, // Puede ser ImageVector o Painter
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


private fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("EEE, d MMM 'a las' hh:mm a", Locale("es", "ES"))
    return sdf.format(this)
}