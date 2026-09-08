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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    var speechRate by remember {
        mutableFloatStateOf(currentSettings.speechRate)
    }

    var voiceEnabled by remember {
        mutableStateOf(currentSettings.voiceEnabled)
    }

    var hapticsEnabled by remember {
        mutableStateOf(currentSettings.hapticEnabled)
    }

    var showDetectionBoxes by remember {
        mutableStateOf(currentSettings.showDetectionBoxes)
    }

    var detectionThreshold by remember {
        mutableFloatStateOf(currentSettings.confidenceThreshold)
    }

    var voiceCommandsEnabled by remember {
        mutableStateOf(currentSettings.voiceCommandsEnabled)
    }

    var hapticIntensity by remember {
        mutableFloatStateOf(currentSettings.hapticIntensity)
    }

    var alertSensitivity by remember {
        mutableStateOf(currentSettings.alertSensitivity)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .semantics {
                    contentDescription = "SenseAI settings screen"
                },
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            SettingsSectionTitle(
                title = "VOICE ASSISTANCE",
                subtitle = "Control how SenseAI speaks to you"
            )

            SettingsSwitchCard(
                title = "Voice Announcements",
                description =
                    "Automatically announce detected objects through the speaker",
                checked = voiceEnabled,
                onCheckedChange = {
                    voiceEnabled = it

                    onSettingsChanged(
                        currentSettings.copy(
                            voiceEnabled = it
                        )
                    )
                }
            )

            SettingsCard {

                Text(
                    text =
                        "Speech Rate: " +
                                String.format("%.1fx", speechRate),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it

                        onSettingsChanged(
                            currentSettings.copy(
                                speechRate = it
                            )
                        )
                    },
                    valueRange = 0.5f..2.0f,
                    steps = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("Slow")
                    Text("Fast")
                }
            }

            SettingsSwitchCard(
                title = "Voice Commands",
                description =
                    "Allow SenseAI to listen for spoken commands",
                checked = voiceCommandsEnabled,
                onCheckedChange = {
                    voiceCommandsEnabled = it

                    onSettingsChanged(
                        currentSettings.copy(
                            voiceCommandsEnabled = it
                        )
                    )
                }
            )

            SettingsSectionTitle(
                title = "HAPTIC FEEDBACK",
                subtitle =
                    "Receive vibration alerts without looking at the screen"
            )

            SettingsSwitchCard(
                title = "Haptic Vibration Alerts",
                description =
                    "Vibrate the phone for approaching obstacles and risk levels",
                checked = hapticsEnabled,
                onCheckedChange = {
                    hapticsEnabled = it

                    onSettingsChanged(
                        currentSettings.copy(
                            hapticEnabled = it
                        )
                    )
                }
            )

            SettingsCard {

                Text(
                    text =
                        "Haptic Intensity: " +
                                "${(hapticIntensity * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = hapticIntensity,
                    onValueChange = {
                        hapticIntensity = it

                        onSettingsChanged(
                            currentSettings.copy(
                                hapticIntensity = it
                            )
                        )
                    },
                    valueRange = 0.2f..1.0f,
                    steps = 3,
                    enabled = hapticsEnabled
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("Gentle")
                    Text("Strong")
                }
            }

            SettingsSectionTitle(
                title = "AI DETECTION",
                subtitle =
                    "Control how sensitive object detection should be"
            )

            SettingsCard {

                Text(
                    text =
                        "Detection Sensitivity: " +
                                "${(detectionThreshold * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                        "Higher values reduce false detections. " +
                                "Lower values detect more possible objects.",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = detectionThreshold,
                    onValueChange = {
                        detectionThreshold = it

                        onSettingsChanged(
                            currentSettings.copy(
                                confidenceThreshold = it
                            )
                        )
                    },
                    valueRange = 0.30f..0.85f,
                    steps = 10
                )
            }

            SettingsCard {

                Text(
                    text = "Alert Sensitivity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text =
                        "Controls how aggressively SenseAI reports potential hazards.",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    AlertSensitivityButton(
                        text = "LOW",
                        selected = alertSensitivity == "LOW",
                        modifier = Modifier.weight(1f)
                    ) {
                        alertSensitivity = "LOW"

                        onSettingsChanged(
                            currentSettings.copy(
                                alertSensitivity = "LOW"
                            )
                        )
                    }

                    AlertSensitivityButton(
                        text = "MEDIUM",
                        selected = alertSensitivity == "MEDIUM",
                        modifier = Modifier.weight(1f)
                    ) {
                        alertSensitivity = "MEDIUM"

                        onSettingsChanged(
                            currentSettings.copy(
                                alertSensitivity = "MEDIUM"
                            )
                        )
                    }

                    AlertSensitivityButton(
                        text = "HIGH",
                        selected = alertSensitivity == "HIGH",
                        modifier = Modifier.weight(1f)
                    ) {
                        alertSensitivity = "HIGH"

                        onSettingsChanged(
                            currentSettings.copy(
                                alertSensitivity = "HIGH"
                            )
                        )
                    }
                }
            }

            SettingsSectionTitle(
                title = "DEVELOPER / DEMO",
                subtitle =
                    "Useful during testing and hackathon demonstrations"
            )

            SettingsSwitchCard(
                title = "Visual AI Overlay",
                description =
                    "Show bounding boxes, direction, distance, movement and confidence",
                checked = showDetectionBoxes,
                onCheckedChange = {
                    showDetectionBoxes = it

                    onSettingsChanged(
                        currentSettings.copy(
                            showDetectionBoxes = it
                        )
                    )
                }
            )

            SettingsCard {

                Text(
                    text = "SenseAI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "See Less. Know More.",
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text =
                        "AI-powered environmental awareness using " +
                                "computer vision, spatial analysis, " +
                                "voice guidance and haptic feedback.",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SettingsSwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun AlertSensitivityButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor =
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
            contentColor =
                if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
        )
    ) {
        Text(
            text = text,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
        )
    }
}