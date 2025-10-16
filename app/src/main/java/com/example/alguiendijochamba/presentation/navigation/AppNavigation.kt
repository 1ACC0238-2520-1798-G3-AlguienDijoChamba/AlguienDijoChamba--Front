package com.example.alguiendijochamba.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alguiendijochamba.presentation.view.AuthScreen
import com.example.alguiendijochamba.presentation.view.OnboardingScreen
import com.example.alguiendijochamba.presentation.view.SignInScreen
import com.example.alguiendijochamba.presentation.view.RegisterScreen
import com.example.alguiendijochamba.presentation.view.CompleteProfileScreen
import com.example.alguiendijochamba.presentation.view.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.OnboardingScreen.route
    ) {
        composable(Screen.OnboardingScreen.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.AuthScreen.route) {
            AuthScreen(navController = navController)
        }
        composable(Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.CompleteProfileScreen.route) {
            CompleteProfileScreen(navController = navController)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
    }
}