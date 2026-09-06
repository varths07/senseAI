package com.example.senseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.senseai.data.repository.SettingsRepository
import com.example.senseai.navigation.AppNavigation
import com.example.senseai.ui.theme.SenseAITheme

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settingsRepository = SettingsRepository(applicationContext)

        setContent {
            SenseAITheme {
                AppNavigation(settingsRepository = settingsRepository)
            }
        }
    }
}