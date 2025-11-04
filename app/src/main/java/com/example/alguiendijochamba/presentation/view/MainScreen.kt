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
    val screens = listOf(BottomBarScreen.Home, BottomBarScreen.Requests, BottomBarScreen.Calendar, BottomBarScreen.Payments, BottomBarScreen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = BottomBarScreen.Home.route, Modifier.padding(innerPadding)) {
            composable(BottomBarScreen.Home.route) { HomeScreen(navController) }
            composable(BottomBarScreen.Requests.route) { Text("Pantalla de Solicitudes") }
            composable(BottomBarScreen.Calendar.route) { Text("Pantalla de Calendario") }
            composable(BottomBarScreen.Payments.route) { Text("Pantalla de Pagos") }
            composable(BottomBarScreen.Profile.route) { ProfileScreen() }
        }
    }
}