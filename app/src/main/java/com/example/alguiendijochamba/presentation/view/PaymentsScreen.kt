package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alguiendijochamba.ui.theme.PrimaryBlue

// Colores auxiliares para esta pantalla
private val TextGray = Color(0xFF8E8E8E)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val CardBorderColor = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Payments & Earnings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción de descarga */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = TextGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(BackgroundWhite)
        ) {
            // 1. Tabs Superiores
            PaymentsTabs()

            Column(modifier = Modifier.padding(24.dp)) {

                // 2. Tarjeta Azul de Saldo Disponible
                BalanceCard()

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Fila de Pendiente y Total Ganado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusCard(
                        title = "Pending",
                        amount = "S/450",
                        icon = Icons.Default.AccessTime,
                        modifier = Modifier.weight(1f)
                    )
                    StatusCard(
                        title = "Total Earned",
                        amount = "S/8750",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Tarjeta de Ganancias Mensuales
                MonthlyEarningsCard()

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Tarjeta de Comisión
                CommissionCard()

                Spacer(modifier = Modifier.height(32.dp))

                // 6. Botón de Retiro
                Button(
                    onClick = { /* Acción de retirar */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CallMade, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Withdraw Funds", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Enlaces inferiores
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Instant Transfer",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        "Bank Transfer",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PaymentsTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab Seleccionada: Overview
        Column(
            modifier = Modifier.weight(1f).clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Overview",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth()
                    .background(PrimaryBlue)
            )
        }
        // Tabs Inactivas
        listOf("History", "Methods").forEach { tab ->
            Column(
                modifier = Modifier.weight(1f).clickable { },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    tab,
                    color = TextGray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF426EF0)) // Un azul un poco más claro como la imagen
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "Available Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "S/1250",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Ready to withdraw",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatusCard(title: String, amount: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, color = TextGray, fontSize = 12.sp)
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(amount, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun MonthlyEarningsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Monthly Earnings", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("This Month", color = TextGray, fontSize = 14.sp)
                Text("S/2100", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Last Month", color = TextGray, fontSize = 14.sp)
                Text("S/1950", color = TextGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso simulada
            LinearProgressIndicator(
                progress = { 0.8f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFE5E9F5),
            )
        }
    }
}

@Composable
fun CommissionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F7FA)) // Borde muy sutil o fondo gris claro
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Platform Commission", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("Applied to each completed job", color = TextGray, fontSize = 12.sp)
            }
            Text("15%", color = PrimaryBlue.copy(alpha = 0.1f), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}