package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alguiendijochamba.ui.theme.PrimaryBlue

// --- Colores ---
private val TextGray = Color(0xFF8E8E8E)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val CardBorderColor = Color(0xFFEEEEEE)
private val IncomeBlue = Color(0xFFE3F2FD)
private val ExpenseRed = Color(0xFFFFEBEE)
private val ArrowBlue = Color(0xFF2196F3)
private val ArrowRed = Color(0xFFF44336)

// --- Modelos de Datos ---
data class TransactionMock(
    val id: String,
    val title: String,
    val subtitle: String?,
    val date: String,
    val transactionId: String,
    val amount: String,
    val type: TransactionType,
    val status: TransactionStatus
)

data class PaymentMethodMock(
    val id: String,
    val name: String,
    val detail: String,
    val isPrimary: Boolean,
    val type: PaymentMethodType // BANK or WALLET
)

enum class PaymentMethodType { BANK, WALLET }
enum class TransactionType { INCOME, EXPENSE, WITHDRAWAL }
enum class TransactionStatus { COMPLETED, PENDING, PROCESSING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController) {
    // Estado de la pestaña (0: Overview, 1: History, 2: Methods)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Datos Mock - Historial
    val historyTransactions = listOf(
        TransactionMock("1", "Plumbing repair service", "Client: María González", "13 oct 2024", "SR-2024-001", "+S/120", TransactionType.INCOME, TransactionStatus.COMPLETED),
        TransactionMock("2", "Platform commission (15%)", null, "13 oct 2024", "SR-2024-001", "S/18", TransactionType.EXPENSE, TransactionStatus.COMPLETED),
        TransactionMock("3", "Electrical installation", "Client: Roberto Silva", "12 oct 2024", "SR-2024-002", "+S/200", TransactionType.INCOME, TransactionStatus.PENDING),
        TransactionMock("4", "Bank transfer withdrawal", null, "11 oct 2024", "WD-2024-003", "S/500", TransactionType.WITHDRAWAL, TransactionStatus.PROCESSING),
        TransactionMock("5", "Carpentry work", "Client: Ana Torres", "10 oct 2024", "SR-2024-003", "+S/300", TransactionType.INCOME, TransactionStatus.COMPLETED)
    )

    // Datos Mock - Métodos de Pago
    val paymentMethods = listOf(
        PaymentMethodMock("1", "Banco de Crédito del Perú", "****1234", true, PaymentMethodType.BANK),
        PaymentMethodMock("2", "Yape", "+51 999 ***789", false, PaymentMethodType.WALLET)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Payments & Earnings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = TextGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {
            // 1. Tabs
            PaymentsTabs(selectedTab) { index -> selectedTab = index }

            // 2. Contenido
            when (selectedTab) {
                0 -> OverviewTabContent()
                1 -> HistoryTabContent(historyTransactions)
                2 -> MethodsTabContent(paymentMethods)
            }
        }
    }
}

// --- Pestañas ---
@Composable
fun PaymentsTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Overview", "History", "Methods").forEachIndexed { index, title ->
            Column(
                modifier = Modifier.weight(1f).clickable { onTabSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    color = if (selectedTab == index) PrimaryBlue else TextGray,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .height(if (selectedTab == index) 3.dp else 1.dp)
                        .fillMaxWidth()
                        .background(if (selectedTab == index) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

// --- PESTAÑA 1: OVERVIEW ---
@Composable
fun OverviewTabContent() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        BalanceCard()
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatusCard(title = "Pending", amount = "S/450", icon = Icons.Default.AccessTime, modifier = Modifier.weight(1f))
            StatusCard(title = "Total Earned", amount = "S/8750", icon = Icons.Default.CheckCircle, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        MonthlyEarningsCard()
        Spacer(modifier = Modifier.height(16.dp))
        CommissionCard()
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CallMade, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Withdraw Funds", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Instant Transfer", fontWeight = FontWeight.Medium)
            Text("Bank Transfer", fontWeight = FontWeight.Medium)
        }
    }
}

// --- PESTAÑA 2: HISTORY ---
@Composable
fun HistoryTabContent(transactions: List<TransactionMock>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChipMock("Week", false)
                FilterChipMock("Month", true)
                FilterChipMock("Quarter", false)
            }
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextGray)
        }
        LazyColumn(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(transactions) { transaction -> TransactionCard(transaction) }
        }
    }
}

// --- PESTAÑA 3: METHODS (NUEVO) ---
@Composable
fun MethodsTabContent(methods: List<PaymentMethodMock>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Payment Methods", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Add Method", color = TextGray, fontSize = 14.sp, modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tarjetas
        methods.forEach { method ->
            PaymentMethodCard(method)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de Configuración
        Text("Payment Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Plano como la imagen
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Auto-withdraw", fontSize = 14.sp, color = Color.Black)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Minimum balance for auto-withdraw", fontSize = 14.sp, color = Color.Black)
                    Text("S/500", fontSize = 14.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(method: PaymentMethodMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F7FA)), // Gris muy claro
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (method.type == PaymentMethodType.BANK) Icons.Default.AccountBalance else Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = PrimaryBlue.copy(alpha = 0.5f) // Azul suave
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(method.name, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(method.detail, fontSize = 12.sp, color = TextGray)
            }

            // Badge 'Primary' y Papelera
            if (method.isPrimary) {
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Primary",
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Icon(
                Icons.Default.DeleteOutline, // Papelera
                contentDescription = "Remove",
                tint = TextGray,
                modifier = Modifier.size(20.dp).clickable { }
            )
        }
    }
}


// --- COMPONENTES AUXILIARES (Overview & History) ---

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF426EF0))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Available Balance", color = Color.White.copy(0.8f), fontSize = 14.sp)
                Icon(Icons.Default.CreditCard, null, tint = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Text("S/1250", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Ready to withdraw", color = Color.White.copy(0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
fun StatusCard(title: String, amount: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(title, color = TextGray, fontSize = 12.sp)
                Icon(icon, null, Modifier.size(16.dp), tint = TextGray)
            }
            Spacer(Modifier.height(8.dp))
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
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("This Month", color = TextGray, fontSize = 14.sp)
                Text("S/2100", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Last Month", color = TextGray, fontSize = 14.sp)
                Text("S/1950", color = TextGray, fontSize = 14.sp)
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(progress = { 0.8f }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = PrimaryBlue, trackColor = Color(0xFFE5E9F5))
        }
    }
}

@Composable
fun CommissionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F7FA))
    ) {
        Row(Modifier.padding(20.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text("Platform Commission", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("Applied to each completed job", color = TextGray, fontSize = 12.sp)
            }
            Text("15%", color = PrimaryBlue.copy(0.1f), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilterChipMock(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PrimaryBlue else Color.Transparent,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)) else null,
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text, fontSize = 12.sp, color = if (isSelected) Color.White else TextGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun TransactionCard(transaction: TransactionMock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val iconBg = if (transaction.type == TransactionType.EXPENSE) ExpenseRed else IncomeBlue
            val iconTint = if (transaction.type == TransactionType.EXPENSE) ArrowRed else ArrowBlue
            val iconVector = if (transaction.type == TransactionType.INCOME) Icons.Default.CallReceived else Icons.Default.CallMade

            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                Icon(iconVector, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                transaction.subtitle?.let { Text(it, fontSize = 12.sp, color = TextGray) }
                Spacer(Modifier.height(4.dp))
                Text("${transaction.date} • ${transaction.transactionId}", fontSize = 11.sp, color = TextGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                val amountColor = if (transaction.type == TransactionType.INCOME) Color(0xFF81D4FA) else Color.Black
                Text(transaction.amount, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = amountColor)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when(transaction.status) {
                        TransactionStatus.COMPLETED -> if(transaction.type == TransactionType.EXPENSE) TextGray else Color(0xFFB9F6CA)
                        TransactionStatus.PENDING -> TextGray
                        TransactionStatus.PROCESSING -> ArrowBlue
                    }
                    val statusIcon = when(transaction.status) {
                        TransactionStatus.COMPLETED -> Icons.Default.CheckCircle
                        TransactionStatus.PENDING -> Icons.Default.AccessTime
                        TransactionStatus.PROCESSING -> Icons.Default.Info
                    }
                    Icon(statusIcon, null, Modifier.size(14.dp), tint = if(transaction.status == TransactionStatus.COMPLETED && transaction.type == TransactionType.INCOME) Color(0xFFE0E0E0) else statusColor)
                    Spacer(Modifier.width(4.dp))
                    Text(transaction.status.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = TextGray)
                }
            }
        }
    }
}