package com.example.dermalens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.dermalens.navigation.DermaLensNavGraph
import com.example.dermalens.navigation.Screen
import com.example.dermalens.ui.MainScaffold
import com.example.dermalens.ui.screens.ConsentScreen
import com.example.dermalens.ui.screens.EditProfileScreen
import com.example.dermalens.ui.screens.HistoryScreen
import com.example.dermalens.ui.screens.HomeScreen
import com.example.dermalens.ui.screens.OnboardingScreen
import com.example.dermalens.ui.screens.ProfileScreen
import com.example.dermalens.ui.screens.SplashScreen
import com.example.dermalens.ui.screens.scan.ScanWizardContainer
import com.example.dermalens.ui.theme.*
import com.example.dermalens.ui.viewmodel.HistoryViewModel
import com.example.dermalens.ui.viewmodel.NavigationTarget
import com.example.dermalens.ui.viewmodel.OnboardingViewModel
import com.example.dermalens.ui.viewmodel.ProfileViewModel
import com.example.dermalens.ui.viewmodel.ScreeningViewModel
import com.example.dermalens.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val screeningViewModel: ScreeningViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermaLensTheme {
                val navController = rememberNavController()

                val profileState by profileViewModel.profile.collectAsState()
                val scanCount by historyViewModel.screeningCount.collectAsState()
                val latestScan by historyViewModel.latestScreening.collectAsState()
                val historyList by historyViewModel.historyList.collectAsState()
                val navTarget by splashViewModel.navigationTarget.collectAsState()

                MainScaffold(navController = navController) { modifier ->
                    DermaLensNavGraph(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = modifier,
                        splashContent = {
                            SplashScreen()
                            LaunchedEffect(navTarget) {
                                when (navTarget) {
                                    NavigationTarget.Consent -> {
                                        navController.navigate(Screen.Consent.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                    NavigationTarget.Onboarding -> {
                                        navController.navigate(Screen.Onboarding.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                    NavigationTarget.Home -> {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                    null -> { /* Preloading ML models & checking baseline in background */ }
                                }
                            }
                        },
                        consentContent = {
                            ConsentScreen(
                                onAgreeClick = {
                                    navController.navigate(Screen.Onboarding.route)
                                }
                            )
                        },
                        onboardingContent = {
                            OnboardingScreen(
                                onComplete = { name, age, sex, skinType, familyHistory, priorCancer, sunburns ->
                                    onboardingViewModel.updateName(name)
                                    onboardingViewModel.updateAge(age)
                                    onboardingViewModel.updateSex(sex)
                                    onboardingViewModel.updateSkinType(skinType)
                                    onboardingViewModel.updateFamilyHistory(familyHistory)
                                    onboardingViewModel.updatePriorCancer(priorCancer)
                                    onboardingViewModel.updateSunburns(sunburns)
                                    onboardingViewModel.completeOnboarding {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        },
                        homeContent = {
                            HomeScreen(
                                userName = profileState?.name ?: "User",
                                scanCount = scanCount,
                                latestScan = latestScan,
                                onStartScanClick = {
                                    screeningViewModel.resetWizard()
                                    navController.navigate(Screen.Scan.route)
                                },
                                onViewLatestScanClick = {
                                    navController.navigate(Screen.History.route)
                                },
                                onViewHistoryClick = {
                                    navController.navigate(Screen.History.route)
                                }
                            )
                        },
                        scanContent = {
                            ScanWizardContainer(
                                viewModel = screeningViewModel,
                                onDismissWizard = {
                                    screeningViewModel.resetWizard()
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                    }
                                }
                            )
                        },
                        historyContent = {
                            HistoryScreen(
                                historyList = historyList,
                                onStartScanClick = {
                                    screeningViewModel.resetWizard()
                                    navController.navigate(Screen.Scan.route)
                                }
                            )
                        },
                        profileContent = {
                            ProfileScreen(
                                profile = profileState,
                                onNavigateToEdit = {
                                    navController.navigate(Screen.EditProfile.route)
                                },
                                onLogoutAndWipe = {
                                    profileViewModel.logoutAndWipe {
                                        navController.navigate(Screen.Consent.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        },
                        editProfileContent = {
                            EditProfileScreen(
                                profile = profileState,
                                onSaveProfile = { updated ->
                                    profileViewModel.saveProfile(updated)
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}