package com.example.senseai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.senseai.data.model.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onBack: () -> Unit
) {
    var speechRate by remember { mutableFloatStateOf(currentSettings.speechRate) }
    var isHapticsEnabled by remember { mutableStateOf(currentSettings.isHapticsEnabled) }
    var showDebugOverlay by remember { mutableStateOf(currentSettings.showDebugOverlay) }
    var detectionThreshold by remember { mutableFloatStateOf(currentSettings.detectionThreshold) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Speech Rate Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Voice Speech Rate: ${String.format("%.1fx", speechRate)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = {
                            speechRate = it
                            onSettingsChanged(currentSettings.copy(speechRate = it))
                        },
                        valueRange = 0.5f..2.0f,
                        steps = 5
                    )
                }
            }

            // Haptics Toggle Section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Haptic Vibration Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Vibrate phone for approaching obstacles and risk levels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isHapticsEnabled,
                        onCheckedChange = {
                            isHapticsEnabled = it
                            onSettingsChanged(currentSettings.copy(isHapticsEnabled = it))
                        }
                    )
                }
            }

            // Hackathon Demo Mode Overlay Toggle
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Hackathon Demo Visual Overlay", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Displays live bounding boxes, distance (~3m), and confidence for judges",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = showDebugOverlay,
                        onCheckedChange = {
                            showDebugOverlay = it
                            onSettingsChanged(currentSettings.copy(showDebugOverlay = it))
                        }
                    )
                }
            }

            // Detection Confidence Threshold
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detection Sensitivity: ${(detectionThreshold * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Slider(
                        value = detectionThreshold,
                        onValueChange = {
                            detectionThreshold = it
                            onSettingsChanged(currentSettings.copy(detectionThreshold = it))
                        },
                        valueRange = 0.30f..0.85f,
                        steps = 10
                    )
                }
            }
        }
    }
}
