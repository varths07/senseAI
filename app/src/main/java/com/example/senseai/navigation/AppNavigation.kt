package com.example.senseai.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                navController.navigate(NavRoutes.CAMERA)
            }
        }

    val microphonePermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                openCamera(
                    context = context,
                    navController = navController
                )
            }
        }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {

        composable(NavRoutes.HOME) {

            HomeScreen(
                onStartSenseAI = {

                    val cameraGranted =
                        PermissionUtils.hasCameraPermission(context)

                    val microphoneGranted =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                    when {

                        !cameraGranted -> {
                            cameraPermissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }

                        !microphoneGranted -> {
                            microphonePermissionLauncher.launch(
                                Manifest.permission.RECORD_AUDIO
                            )
                        }

                        else -> {
                            navController.navigate(
                                NavRoutes.CAMERA
                            )
                        }
                    }
                },

                onOpenSettings = {
                    navController.navigate(
                        NavRoutes.SETTINGS
                    )
                }
            )
        }

        composable(NavRoutes.CAMERA) {

            CameraScreen(
                appSettings = appSettings,

                onStopAssistance = {
                    navController.popBackStack(
                        NavRoutes.HOME,
                        inclusive = false
                    )
                }
            )
        }

        composable(NavRoutes.SETTINGS) {

            SettingsScreen(
                currentSettings = appSettings,

                onSettingsChanged = { updatedSettings ->
                    settingsRepository.updateSettings(
                        updatedSettings
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun openCamera(
    context: Context,
    navController: androidx.navigation.NavHostController
) {
    val cameraGranted =
        PermissionUtils.hasCameraPermission(context)

    if (cameraGranted) {
        navController.navigate(NavRoutes.CAMERA)
    }
}s