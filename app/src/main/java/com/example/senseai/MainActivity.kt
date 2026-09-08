package com.example.senseai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.senseai.data.repository.SettingsRepository
import com.example.senseai.navigation.AppNavigation
import com.example.senseai.ui.theme.SenseAITheme

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 1001
        private const val AUDIO_PERMISSION_REQUEST = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SenseAI_v3", "MainActivity: onCreate")

        enableEdgeToEdge()

        settingsRepository =
            SettingsRepository(applicationContext)

        requestRequiredPermissions()

        setContent {
            SenseAITheme {
                AppNavigation(
                    settingsRepository = settingsRepository
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("SenseAI_v3", "MainActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SenseAI_v3", "MainActivity: onPause")
    }

    private fun requestRequiredPermissions() {

        val permissionsToRequest =
            mutableListOf<String>()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(
                Manifest.permission.CAMERA
            )
        }

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(
                Manifest.permission.RECORD_AUDIO
            )
        }

        if (permissionsToRequest.isNotEmpty()) {

            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION_REQUEST
            )
        }
    }
}
