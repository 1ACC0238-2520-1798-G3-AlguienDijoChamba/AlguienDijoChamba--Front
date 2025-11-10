package com.example.alguiendijochamba.presentation.navigation

sealed class Screen(val route: String) {
    object OnboardingScreen : Screen("onboarding_screen")
    object AuthScreen : Screen("auth_screen")
    object SignInScreen : Screen("sign_in_screen")
    object RegisterScreen : Screen("register_screen")
    object CompleteProfileScreen : Screen("complete_profile_screen")
    object MainScreen : Screen("main_screen")
}