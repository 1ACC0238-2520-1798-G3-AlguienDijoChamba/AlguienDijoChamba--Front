package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alguiendijochamba.domain.model.JobRequest
import com.example.alguiendijochamba.presentation.viewmodel.HomeViewModel
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import com.example.alguiendijochamba.presentation.viewmodel.HomeUiState // <-- Agrega esta línea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedBottomNavItem by remember { mutableStateOf(0) }
    val bottomNavItems = listOf("Home", "Requests", "Calendar", "Payments", "Profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Home
                                    1 -> Icons.Default.ListAlt
                                    2 -> Icons.Default.CalendarToday
                                    3 -> Icons.Default.Payment
                                    else -> Icons.Default.Person
                                },
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = selectedBottomNavItem == index,
                        onClick = { selectedBottomNavItem = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F2F5)) // Un gris claro de fondo
        ) {
            item { WelcomeHeader(uiState) }
            item {
                val tabs = listOf("Requests", "Balance", "Earnings")
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
                    1 -> BalanceTabContent() // Crear este Composable
                    2 -> EarningsTabContent() // Crear este Composable
                }
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
                    Text("Welcome back,", color = Color.White.copy(alpha = 0.8f))
                    Text(uiState.userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    BadgedBox(badge = { Badge { Text("${uiState.messageCount}") } }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Messages", tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    BadgedBox(badge = { Badge { Text("${uiState.notificationCount}") } }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
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
                        Text("Completed Jobs: ${uiState.completedJobs}", color = Color.White.copy(alpha = 0.8f))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                            Text(" ${uiState.starRating}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text("Available Balance: S/${uiState.availableBalance}", color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
fun RequestsTabContent(requests: List<JobRequest>, viewModel: HomeViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("New Requests", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        if (requests.isEmpty()) {
            Text("No new requests at the moment.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
        } else {
            requests.forEach { request ->
                JobRequestCard(request = request, onAccept = { viewModel.acceptRequest(it) }, onDecline = { viewModel.declineRequest(it) })
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun JobRequestCard(request: JobRequest, onAccept: (JobRequest) -> Unit, onDecline: (JobRequest) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(request.clientName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if(request.isUrgent) Badge(containerColor = Color.Red.copy(alpha = 0.1f)) { Text("Urgent", color = Color.Red) }
                if(request.isPending) Badge() { Text("Pending") }
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
                Text(request.dateTime.toString(), style = MaterialTheme.typography.bodyMedium) // Formatear la fecha
            }
            Spacer(Modifier.height(8.dp))
            Text(request.description)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("S/${request.price}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Row {
                    OutlinedButton(onClick = { onDecline(request) }) { Text("Decline") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onAccept(request) }) { Text("Accept") }
                }
            }
        }
    }
}

// Placeholder Composables for other tabs
@Composable fun BalanceTabContent() { Text("Balance Content", modifier = Modifier.padding(16.dp)) }
@Composable fun EarningsTabContent() { Text("Earnings Content", modifier = Modifier.padding(16.dp)) }