package com.example.dermalens.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dermalens.R
import com.example.dermalens.navigation.BottomNavItem
import com.example.dermalens.navigation.Screen
import com.example.dermalens.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isFullScreenRoute = currentRoute in listOf(
        Screen.Splash.route,
        Screen.Consent.route,
        Screen.Onboarding.route,
        Screen.Scan.route,
        Screen.Profile.route,
        Screen.EditProfile.route
    )

    val showBottomBar = !isFullScreenRoute
    val showTopBar = currentRoute !in listOf(
        Screen.Splash.route,
        Screen.Consent.route,
        Screen.Onboarding.route,
        Screen.Scan.route,
        Screen.EditProfile.route
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentRoute != Screen.Profile.route) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "DermaLens Logo",
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                text = if (currentRoute == Screen.Profile.route) "Profile" else "DermaLens",
                                fontWeight = FontWeight.Bold,
                                color = MedicalPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        if (currentRoute == Screen.Profile.route) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MedicalOnSurface
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentRoute != Screen.Profile.route) {
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.Profile.route) {
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    tint = MedicalPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MedicalBackground
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MedicalSurface,
                    tonalElevation = 0.dp
                ) {
                    BottomNavItem.items.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MedicalOnPrimaryContainer,
                                selectedTextColor = MedicalPrimary,
                                indicatorColor = MedicalPrimaryContainer,
                                unselectedIconColor = MedicalOutline,
                                unselectedTextColor = MedicalOnSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        containerColor = MedicalBackground
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
