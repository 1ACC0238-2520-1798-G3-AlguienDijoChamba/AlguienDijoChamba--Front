package com.example.alguiendijochamba.presentation.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen("home", "Home", Icons.Default.Home)
    object Requests : BottomBarScreen("requests", "Requests", Icons.AutoMirrored.Filled.ListAlt)
    object Calendar : BottomBarScreen("calendar", "Calendar", Icons.Default.CalendarToday)
    object Payments : BottomBarScreen("payments", "Payments", Icons.Default.Payment)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Lista ordenada de las pantallas en el menú inferior
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Requests,
        BottomBarScreen.Calendar,
        BottomBarScreen.Payments,
        BottomBarScreen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        // Marca el ícono como seleccionado si coincide con la ruta actual
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Evita que se acumulen pantallas en el back stack al cambiar de tab
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                // Evita múltiples copias de la misma pantalla si se pulsa repetidamente
                                launchSingleTop = true
                                // Restaura el estado de la pantalla (scroll, inputs, etc.)
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Grafo de navegación principal
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home
            composable(BottomBarScreen.Home.route) { HomeScreen(navController) }

            // 2. Solicitudes
            composable(BottomBarScreen.Requests.route) { RequestsScreen(navController) }

            // 3. Calendario (Implementado)
            composable(BottomBarScreen.Calendar.route) { CalendarScreen(navController) }

            // 4. Pagos (¡NUEVO! Implementado)
            composable(BottomBarScreen.Payments.route) { PaymentsScreen(navController) }

            // 5. Perfil
            composable(BottomBarScreen.Profile.route) { ProfileScreen(navController) }
        }
    }
}