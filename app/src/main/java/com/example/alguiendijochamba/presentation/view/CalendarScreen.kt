package com.example.alguiendijochamba.presentation.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alguiendijochamba.R
import com.example.alguiendijochamba.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// --- Colores del Diseño ---
private val PastelPink = Color(0xFFFFB5B5)
private val PastelPurple = Color(0xFFC6B5FF)
private val PastelYellow = Color(0xFFFFF59D)
private val TextGray = Color(0xFF8E8E8E)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFEEEEEE)

// Colores de Tags (Etiquetas)
private val TagGreen = Color(0xFFB9F6CA)
private val TagCyan = Color(0xFFB2EBF2)
private val TagPurple = Color(0xFFE1BEE7)
private val TagOrange = Color(0xFFFFE0B2)
private val TagRed = Color(0xFFFFCCBC) // Para tags de urgencia/leak

// --- Modelos de Datos ---
data class CalendarEventMock(
    val id: Int,
    val day: String,
    val dayName: String, // En Reminder se usa como "Mes" (ej: January)
    val title: String,
    val timeRange: String,
    val clientName: String,
    val price: String,
    val color: Color,
    val tags: List<TagMock>
)

data class TagMock(val text: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    // --- ESTADO ---
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calendar, 1: Reminder
    var currentCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var expandedEventId by remember { mutableStateOf<Int?>(null) }

    // Datos simulados para CALENDAR (Vista 1)
    val calendarEvents = listOf(
        CalendarEventMock(1, "10", "Tuesday", "Water Heater Maintenance", "09:00 AM - 10:30 PM", "Jose Rojas Perez", "$ 250", PastelPink, listOf(TagMock("High Priority", TagGreen), TagMock("Plumbing", TagCyan), TagMock("Home", TagPurple))),
        CalendarEventMock(2, "25", "Tuesday", "Water Heater Maintenance", "10:00 AM - 12:00 PM", "Maria Lopez", "$ 180", PastelPurple, listOf(TagMock("Plumbing", TagCyan))),
        CalendarEventMock(3, "31", "Tuesday", "Water Heater Maintenance", "02:00 PM - 04:00 PM", "Carlos Ruiz", "$ 300", PastelYellow, listOf(TagMock("Maintenance", TagOrange), TagMock("Urgent", TagGreen)))
    )

    // Datos simulados para REMINDER (Vista 2 - Según tu imagen)
    val reminderEvents = listOf(
        CalendarEventMock(101, "10", "January", "Water Heater Maintenance", "09:00 PM - 10:30 PM", "Jose Rojas Perez", "$ 250", BackgroundWhite, listOf(TagMock("High Priority", TagGreen), TagMock("Plumbing", TagCyan), TagMock("Home", TagPurple), TagMock("Maintenance", TagOrange))),
        CalendarEventMock(102, "16", "January", "Fix leaking pipe in kitchen", "04:00 PM - 05:30 PM", "Sebastian Maguiña Rojas", "$ 100", BackgroundWhite, listOf(TagMock("Standard", TagGreen), TagMock("Plumbing", TagCyan), TagMock("Kitchen", TagPurple), TagMock("Leak", TagOrange))),
        CalendarEventMock(103, "1", "February", "Unclog bathroom sink", "01:00 PM - 02:30 PM", "Ricardo Jose Martinez Leyes", "$ 250", BackgroundWhite, listOf(TagMock("Follow-up", TagGreen), TagMock("Plumbing", TagCyan), TagMock("Bathroom", TagPurple), TagMock("Drain", TagOrange))),
        CalendarEventMock(104, "10", "February", "Install new shower faucet", "10:00 AM - 11:30 AM", "Maria Vegas Cuevas", "$ 250", BackgroundWhite, listOf(TagMock("Low", TagGreen), TagMock("Plumbing", TagCyan), TagMock("Bathroom", TagPurple), TagMock("Installation", TagOrange)))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextGray)
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
            // 1. Tabs Interactivas
            CalendarTabs(selectedTab) { index -> selectedTab = index }

            Spacer(modifier = Modifier.height(16.dp))

            // Lógica de cambio de vista
            if (selectedTab == 0) {
                // --- VISTA CALENDARIO ---
                MonthSelector(
                    currentCalendar = currentCalendar,
                    onPrevious = {
                        val prev = currentCalendar.clone() as Calendar
                        prev.add(Calendar.MONTH, -1)
                        currentCalendar = prev
                    },
                    onNext = {
                        val next = currentCalendar.clone() as Calendar
                        next.add(Calendar.MONTH, 1)
                        currentCalendar = next
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CalendarGridSection(currentCalendar)
                Spacer(modifier = Modifier.height(24.dp))
                EventListSection(
                    events = calendarEvents,
                    expandedEventId = expandedEventId,
                    onEventClick = { id -> expandedEventId = if (expandedEventId == id) null else id }
                )
            } else {
                // --- VISTA REMINDER (NUEVA) ---
                ReminderListSection(events = reminderEvents)
            }
        }
    }
}

// --- Componentes UI ---

@Composable
fun CalendarTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Tab Calendar
        Column(
            modifier = Modifier.weight(1f).clickable { onTabSelected(0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Calendar",
                color = if (selectedTab == 0) PrimaryBlue else TextGray,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Box(
                modifier = Modifier
                    .height(if (selectedTab == 0) 3.dp else 1.dp)
                    .fillMaxWidth()
                    .background(if (selectedTab == 0) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
            )
        }
        // Tab Reminder
        Column(
            modifier = Modifier.weight(1f).clickable { onTabSelected(1) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Reminder",
                color = if (selectedTab == 1) PrimaryBlue else TextGray,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Box(
                modifier = Modifier
                    .height(if (selectedTab == 1) 3.dp else 1.dp)
                    .fillMaxWidth()
                    .background(if (selectedTab == 1) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
            )
        }
    }
}

// --- Componente NUEVO para la lista de Recordatorios ---
@Composable
fun ReminderListSection(events: List<CalendarEventMock>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        items(events) { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top // Alineación arriba para coincidir con la imagen
            ) {
                // Columna Fecha (Izquierda) - Estilo específico para Reminder
                Column(
                    modifier = Modifier.width(50.dp).padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(event.dayName, fontSize = 11.sp, color = TextGray) // Mes (e.g., January)
                    Text(event.day, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black.copy(alpha = 0.8f)) // Día (e.g., 10)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Tarjeta detallada (siempre visible como "expandida")
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)), // Borde gris sutil
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    // Reutilizamos el contenido expandido porque el diseño es idéntico
                    // pero forzamos que la flecha sea hacia abajo o simplemente un icono estático si se prefiere
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = event.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = event.timeRange,
                            fontSize = 11.sp, // Un poco más pequeño como en la imagen
                            color = TextGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_placeholder),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = event.clientName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = event.price,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            event.tags.take(4).forEach { tag -> // Limitar a 4 tags por espacio
                                Surface(
                                    color = tag.color,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = tag.text,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Componentes reutilizados del Calendario ---

@Composable
fun MonthSelector(currentCalendar: Calendar, onPrevious: () -> Unit, onNext: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    val monthYearString = dateFormat.format(currentCalendar.time)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(monthYearString, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun CalendarGridSection(currentCalendar: Calendar) {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

    val tempCal = currentCalendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)
    val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
    val startOffset = dayOfWeek - 1
    val totalCells = startOffset + daysInMonth
    val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                daysOfWeek.forEach { day ->
                    Text(text = day, color = Color.LightGray, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (col in 0..6) {
                        val dayNumber = (row * 7 + col) - startOffset + 1
                        val isValidDay = dayNumber in 1..daysInMonth

                        val isSpecial = isValidDay && (dayNumber == 10 || dayNumber == 25 || dayNumber == 31)
                        val backgroundColor = when (dayNumber) {
                            10 -> PastelPink
                            25 -> PastelPurple
                            31 -> PastelYellow
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSpecial) backgroundColor else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isValidDay) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventListSection(
    events: List<CalendarEventMock>,
    expandedEventId: Int?,
    onEventClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            val isExpanded = event.id == expandedEventId

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = if (isExpanded) Alignment.Top else Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(50.dp).padding(top = if(isExpanded) 8.dp else 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(event.dayName, fontSize = 12.sp, color = Color.Gray)
                    Text(event.day, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
                        .clickable { onEventClick(event.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) BackgroundWhite else event.color
                    ),
                    border = if (isExpanded) BorderStroke(1.dp, event.color) else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isExpanded) {
                        ExpandedEventContent(event)
                    } else {
                        CollapsedEventContent(event)
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}

@Composable
fun CollapsedEventContent(event: CalendarEventMock) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = event.title,
            fontSize = 14.sp,
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

@Composable
fun ExpandedEventContent(event: CalendarEventMock) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Collapse",
                tint = Color.Gray
            )
        }

        Text(
            text = event.timeRange,
            fontSize = 13.sp,
            color = TextGray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = event.clientName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = event.price,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            event.tags.forEach { tag ->
                Surface(
                    color = tag.color,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = tag.text,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}