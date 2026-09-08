package com.example.senseai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
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

    val aiEngine = remember {
        AIEngine(context)
    }

    val ttsManager = remember {
        TextToSpeechManager(context)
    }

    val hapticManager = remember {
        HapticFeedbackManager(context)
    }

    val cameraManager = remember {
        CameraManager(
            context,
            lifecycleOwner
        )
    }

    var sceneResult by remember {
        mutableStateOf<SceneResult?>(null)
    }

    var isAnalyzing by remember {
        mutableStateOf(true)
    }

    var frameWidth by remember {
        mutableStateOf(480)
    }

    var frameHeight by remember {
        mutableStateOf(640)
    }

    var ocrText by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Apply speech rate from Settings.
     */
    LaunchedEffect(appSettings.speechRate) {

        ttsManager.setSpeechRate(
            appSettings.speechRate
        )
    }

    /*
     * Clean up everything when leaving
     * the camera screen.
     */
    DisposableEffect(Unit) {

        onDispose {

            isAnalyzing = false

            cameraManager.stopCamera()

            aiEngine.close()

            ttsManager.stop()

            ttsManager.shutdown()

            hapticManager.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        /*
         * LIVE CAMERA
         */
        CameraPreview(
            onPreviewViewCreated = { previewView ->

                cameraManager.startCamera(
                    previewView = previewView,

                    onFrameAvailable = { imageProxy ->

                        if (!isAnalyzing) {

                            imageProxy.close()

                            return@startCamera
                        }

                        frameWidth =
                            imageProxy.width

                        frameHeight =
                            imageProxy.height

                        aiEngine.processFrame(

                            imageProxy = imageProxy,

                            onSceneResult = { result ->

                                sceneResult = result

                                val primaryObject =
                                    result.primaryObject

                                val alert =
                                    result.primaryAlertText

                                if (
                                    alert != null &&
                                    primaryObject != null
                                ) {

                                    val risk =
                                        primaryObject
                                            .trust
                                            .riskLevel

                                    val urgent =
                                        risk == RiskLevel.HIGH

                                    /*
                                     * Speak the AI result.
                                     */
                                    ttsManager.speak(
                                        text = alert,
                                        urgent = urgent
                                    )

                                    /*
                                     * Vibrate according
                                     * to danger level.
                                     */
                                    hapticManager
                                        .triggerHapticForRisk(
                                            riskLevel = risk,
                                            isEnabled =
                                                appSettings
                                                    .isHapticsEnabled
                                        )
                                }
                            },

                            onError = { error ->

                                Logger.e(
                                    "Frame processing failed",
                                    error
                                )
                            }
                        )
                    },

                    onError = { error ->

                        Logger.e(
                            "Camera error",
                            error
                        )

                        Toast.makeText(
                            context,
                            "Camera error: ${error.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )

        /*
         * DEBUG DETECTION OVERLAY
         */
        if (
            appSettings.showDebugOverlay &&
            sceneResult != null
        ) {

            val objects =
                buildList {

                    sceneResult
                        ?.primaryObject
                        ?.let {
                            add(it)
                        }

                    sceneResult
                        ?.secondaryObjects
                        ?.let {
                            addAll(it)
                        }
                }

            DetectionOverlay(
                trackedObjects = objects,
                imageWidth = frameWidth,
                imageHeight = frameHeight
            )
        }

        /*
         * STATUS
         */
        val primaryObject =
            sceneResult?.primaryObject

        StatusIndicator(

            isActive = isAnalyzing,

            statusMessage =
                ocrText
                    ?: sceneResult
                        ?.fullSceneDescription
                    ?: "Analyzing environment...",

            trustLevel =
                primaryObject
                    ?.trust
                    ?.trustLevel
                    ?: TrustLevel.HIGH,

            riskLevel =
                primaryObject
                    ?.trust
                    ?.riskLevel
                    ?: RiskLevel.SAFE,

            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        /*
         * BOTTOM CONTROLS
         */
        Column(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            /*
             * OCR BUTTON
             */
            SecondaryActionButton(

                text = "READ THIS TEXT (OCR)",

                icon =
                    Icons.Default.TextFields,

                onClick = {

                    ttsManager.speakUrgent(
                        "Reading text"
                    )

                    Toast.makeText(
                        context,
                        "Position text in camera view",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            /*
             * STOP BUTTON
             */
            PrimaryActionButton(

                text = "STOP ASSISTANCE",

                icon =
                    Icons.Default.Stop,

                onClick = {

                    isAnalyzing = false

                    ttsManager.stop()

                    hapticManager.stop()

                    cameraManager.stopCamera()

                    onStopAssistance()
                },

                isDanger = true
            )
        }
    }
}