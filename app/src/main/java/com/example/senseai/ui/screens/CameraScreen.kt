package com.example.senseai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.example.senseai.ai.AIEngine
import com.example.senseai.camera.CameraManager
import com.example.senseai.data.model.AppSettings
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.SceneResult
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.haptics.HapticFeedbackManager
import com.example.senseai.ui.components.CameraPreview
import com.example.senseai.ui.components.DetectionOverlay
import com.example.senseai.ui.components.PrimaryActionButton
import com.example.senseai.ui.components.SecondaryActionButton
import com.example.senseai.ui.components.StatusIndicator
import com.example.senseai.utils.Logger
import com.example.senseai.voice.TextToSpeechManager

@Composable
fun CameraScreen(
    appSettings: AppSettings,
    onStopAssistance: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val aiEngine = remember { AIEngine(context) }
    val ttsManager = remember { TextToSpeechManager(context) }
    val hapticManager = remember { HapticFeedbackManager(context) }
    val cameraManager = remember { CameraManager(context, lifecycleOwner) }

    var sceneResult by remember { mutableStateOf<SceneResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(true) }
    var frameWidth by remember { mutableStateOf(480) }
    var frameHeight by remember { mutableStateOf(640) }
    var ocrText by remember { mutableStateOf<String?>(null) }

    // Synchronize TTS speech rate from settings
    LaunchedEffect(appSettings.speechRate) {
        ttsManager.setSpeechRate(appSettings.speechRate)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopCamera()
            aiEngine.close()
            ttsManager.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // CameraX Live Preview View
        CameraPreview(
            onPreviewViewCreated = { previewView ->
                cameraManager.startCamera(
                    previewView = previewView,
                    onFrameAvailable = { imageProxy ->
                        if (!isAnalyzing) {
                            imageProxy.close()
                            return@startCamera
                        }

                        frameWidth = imageProxy.width
                        frameHeight = imageProxy.height

                        aiEngine.processFrame(
                            imageProxy = imageProxy,
                            onSceneResult = { result ->
                                sceneResult = result
                                val primaryAlert = result.primaryAlertText
                                val primaryObj = result.primaryObject

                                if (primaryAlert != null && primaryObj != null) {
                                    val isHighRisk = primaryObj.trust.riskLevel == RiskLevel.HIGH
                                    ttsManager.speak(
                                        text = primaryAlert,
                                        isUrgent = isHighRisk
                                    )
                                    hapticManager.triggerHapticForRisk(
                                        riskLevel = primaryObj.trust.riskLevel,
                                        isEnabled = appSettings.isHapticsEnabled
                                    )
                                }
                            },
                            onError = { e ->
                                Logger.e("Frame processing failed in CameraScreen", e)
                            }
                        )
                    },
                    onError = { e ->
                        Toast.makeText(context, "Camera error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )

        // Visual Bounding Box Overlay for Demo / Judges
        if (appSettings.showDebugOverlay && sceneResult != null) {
            DetectionOverlay(
                trackedObjects = sceneResult?.secondaryObjects?.let { sec ->
                    sceneResult?.primaryObject?.let { prim -> listOf(prim) + sec } ?: sec
                } ?: emptyList(),
                imageWidth = frameWidth,
                imageHeight = frameHeight
            )
        }

        // Top Status Indicator Card
        val primaryObj = sceneResult?.primaryObject
        StatusIndicator(
            isActive = isAnalyzing,
            statusMessage = ocrText ?: sceneResult?.fullSceneDescription ?: "Analyzing environment...",
            trustLevel = primaryObj?.trust?.trustLevel ?: TrustLevel.HIGH,
            riskLevel = primaryObj?.trust?.riskLevel ?: RiskLevel.SAFE,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryActionButton(
                text = "READ THIS TEXT (OCR)",
                icon = Icons.Default.TextFields,
                onClick = {
                    ttsManager.speak("Reading text", isUrgent = true)
                    Toast.makeText(context, "Position text in camera view", Toast.LENGTH_SHORT).show()
                }
            )

            PrimaryActionButton(
                text = "STOP ASSISTANCE",
                icon = Icons.Default.Stop,
                onClick = {
                    ttsManager.stop()
                    onStopAssistance()
                },
                isDanger = true
            )
        }
    }
}
