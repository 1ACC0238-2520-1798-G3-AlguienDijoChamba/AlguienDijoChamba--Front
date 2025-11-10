package com.example.alguiendijochamba.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.alguiendijochamba.presentation.view.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.OnboardingScreen.route) {
        composable(Screen.OnboardingScreen.route) { OnboardingScreen(navController) }
        composable(Screen.AuthScreen.route) { AuthScreen(navController) }
        composable(Screen.SignInScreen.route) { SignInScreen(navController) }
        composable(Screen.RegisterScreen.route) { RegisterScreen(navController) }
        composable(Screen.CompleteProfileScreen.route) { CompleteProfileScreen(navController) }
        composable(Screen.MainScreen.route) { MainScreen() }
    }
}