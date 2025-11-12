package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.NavController
import com.example.alguiendijochamba.ui.theme.PrimaryBlue

// Colores específicos del diseño (Pasteles)
private val PastelPink = Color(0xFFFFB5B5)
private val PastelPurple = Color(0xFFC6B5FF)
private val PastelYellow = Color(0xFFFFF59D)
private val TextGray = Color(0xFF8E8E8E)
private val BackgroundWhite = Color(0xFFFFFFFF)

data class CalendarEventMock(
    val day: String,
    val dayName: String,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    // Datos simulados para igualar la imagen
    val events = listOf(
        CalendarEventMock("10", "Tuesday", "Water Heater Maintenance", PastelPink),
        CalendarEventMock("25", "Tuesday", "Water Heater Maintenance", PastelPurple),
        CalendarEventMock("31", "Tuesday", "Water Heater Maintenance", PastelYellow)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Calendar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Settings action */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextGray)
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
                .background(BackgroundWhite)
        ) {
            // 1. Tabs (Calendar / Reminder)
            CalendarTabs()

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Selector de Mes
            MonthSelector()

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Grilla del Calendario
            CalendarGridSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Lista de Eventos
            EventListSection(events)
        }
    }
}

@Composable
fun CalendarTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Calendar",
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
        Column(
            modifier = Modifier.weight(1f).clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Reminder",
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

@Composable
fun MonthSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.KeyboardArrowLeft, // Corrección: El ícono original era AutoMirrored
            contentDescription = "Prev",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            "January",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CalendarGridSection() {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    // Matriz visual basada en la imagen (Enero empieza Domingo 1)
    val days = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7"),
        listOf("8", "9", "10", "11", "12", "13", "14"),
        listOf("15", "16", "17", "18", "19", "20", "21"),
        listOf("22", "23", "24", "25", "26", "27", "28"),
        listOf("29", "31", "1", "2", "3", "4", "5") // Nota: puse 31 directo para simular la imagen
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera Días de la Semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Días
            days.forEachIndexed { index, week ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val isNextMonth = index == 4 && day.toInt() < 10
                        val isSpecial = day == "10" || day == "25" || day == "31"
                        val backgroundColor = when(day) {
                            "10" -> PastelPink
                            "25" -> PastelPurple
                            "31" -> PastelYellow
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isNextMonth) backgroundColor else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontWeight = FontWeight.Bold,
                                color = if (isNextMonth) Color.LightGray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventListSection(events: List<CalendarEventMock>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna Fecha
                Column(
                    modifier = Modifier.width(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(event.dayName, fontSize = 12.sp, color = Color.Gray)
                    Text(event.day, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Tarjeta Evento
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = event.color),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = event.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}