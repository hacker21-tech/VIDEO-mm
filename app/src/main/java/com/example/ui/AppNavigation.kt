package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    
    var showBottomMenu by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (showBottomMenu) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Applets") },
                        selected = currentRoute == "dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Creator") },
                        label = { Text("Creator") },
                        selected = currentRoute == "creator",
                        onClick = {
                            navController.navigate("creator") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MusicNote, contentDescription = "Audio") },
                        label = { Text("Audio") },
                        selected = currentRoute == "audio",
                        onClick = {
                            navController.navigate("audio") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.PrivacyTip, contentDescription = "Privacy") },
                        label = { Text("Privacy") },
                        selected = currentRoute == "privacy",
                        onClick = {
                            navController.navigate("privacy") {
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
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                showBottomMenu = false
                LoginScreen(onLoginSuccess = {
                    showBottomMenu = true
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            composable("dashboard") {
                showBottomMenu = true
                DashboardScreen(viewModel, innerPadding)
            }
            composable("creator") {
                showBottomMenu = true
                VideoCreatorScreen(viewModel, innerPadding)
            }
            composable("audio") {
                showBottomMenu = true
                AudioLibraryScreen(viewModel, innerPadding)
            }
            composable("privacy") {
                showBottomMenu = true
                PrivacyScreen(viewModel, innerPadding, isDarkTheme, onDarkThemeChange)
            }
        }
    }
}
