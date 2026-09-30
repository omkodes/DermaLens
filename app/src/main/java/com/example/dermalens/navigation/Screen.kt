package com.example.dermalens.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Consent : Screen("consent")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Scan : Screen("scan")
    object History : Screen("history")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Chat : Screen("chat/{screeningId}") {
        fun createRoute(screeningId: Long): String = "chat/$screeningId"
    }
}
