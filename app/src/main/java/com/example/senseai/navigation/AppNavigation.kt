package com.example.senseai.navigation

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.senseai.data.model.AppSettings
import com.example.senseai.data.repository.SettingsRepository
import com.example.senseai.ui.screens.CameraScreen
import com.example.senseai.ui.screens.HomeScreen
import com.example.senseai.ui.screens.SettingsScreen
import com.example.senseai.utils.PermissionUtils

object NavRoutes {
    const val HOME = "home"
    const val CAMERA = "camera"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appSettings by settingsRepository.settings.collectAsState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate(NavRoutes.CAMERA)
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onStartSenseAI = {
                    if (PermissionUtils.hasCameraPermission(context)) {
                        navController.navigate(NavRoutes.CAMERA)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onOpenSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }

        composable(NavRoutes.CAMERA) {
            CameraScreen(
                appSettings = appSettings,
                onStopAssistance = {
                    navController.popBackStack(NavRoutes.HOME, inclusive = false)
                }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                currentSettings = appSettings,
                onSettingsChanged = { updated ->
                    settingsRepository.updateSettings(updated)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
