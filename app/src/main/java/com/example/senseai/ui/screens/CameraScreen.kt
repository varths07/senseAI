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
import com.example.senseai.voice.VoiceManager

@Composable
fun CameraScreen(
    appSettings: AppSettings,
    onStopAssistance: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    /*
     * ==============================
     * AI ENGINE
     * ==============================
     */
    val aiEngine = remember {
        AIEngine(context)
    }

    /*
     * ==============================
     * TEXT TO SPEECH
     * ==============================
     */
    val voiceManager = remember {
        VoiceManager(context)
    }

    /*
     * ==============================
     * HAPTIC MANAGER
     * ==============================
     */
    val hapticManager = remember {
        HapticFeedbackManager(context)
    }

    /*
     * ==============================
     * CAMERA MANAGER
     * ==============================
     */
    val cameraManager = remember {
        CameraManager(
            context = context,
            lifecycleOwner = lifecycleOwner
        )
    }

    /*
     * ==============================
     * STATE
     * ==============================
     */
    val isModelAvailable = remember {
        aiEngine.modelManager.isModelAvailable()
    }

    LaunchedEffect(Unit) {
        if (!isModelAvailable) {
            Toast.makeText(
                context,
                "AI Model not found. Please download it in settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    var sceneResult by remember {
        mutableStateOf<SceneResult?>(null)
    }

    var isAnalyzing by remember {
        mutableStateOf(true)
    }

    var frameWidth by remember {
        mutableIntStateOf(480)
    }

    var frameHeight by remember {
        mutableIntStateOf(640)
    }

    var ocrText by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * ==============================
     * SPEECH SETTINGS
     * ==============================
     */
    LaunchedEffect(appSettings.speechRate) {

        voiceManager.setSpeechRate(
            appSettings.validSpeechRate
        )
    }

    /*
     * ==============================
     * CLEANUP
     * ==============================
     */
    DisposableEffect(Unit) {

        onDispose {

            isAnalyzing = false

            cameraManager.stopCamera()

            aiEngine.close()

            voiceManager.stop()

            voiceManager.shutdown()

            hapticManager.stop()
        }
    }

    /*
     * ==============================
     * MAIN SCREEN
     * ==============================
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        /*
         * ==============================
         * CAMERA PREVIEW
         * ==============================
         */
        CameraPreview(
            onPreviewViewCreated = { previewView ->

                cameraManager.startCamera(

                    previewView = previewView,

                    onFrameAvailable = { imageProxy ->

                        /*
                         * If analysis has stopped,
                         * release the frame.
                         */
                        if (!isAnalyzing) {

                            imageProxy.close()

                            return@startCamera
                        }

                        /*
                         * Save image dimensions.
                         */
                        frameWidth =
                            imageProxy.width

                        frameHeight =
                            imageProxy.height

                        /*
                         * Send frame to AI.
                         */
                        aiEngine.processFrame(

                            imageProxy = imageProxy,

                            onSceneResult = { result ->
                                Logger.d("CameraScreen: onSceneResult received")
                                sceneResult = result

                                /*
                                 * VOICE ANNOUNCEMENTS
                                 */
                                if (appSettings.voiceEnabled) {

                                    val detections = buildList {
                                        result.primaryObject?.let { add(it.detection) }
                                        addAll(result.secondaryObjects.map { it.detection })
                                    }

                                    voiceManager.announceDetections(
                                        detections = detections,
                                        minIntervalMs = appSettings.validRepeatAlertDelayMs,
                                        confidenceThreshold = appSettings.validConfidenceThreshold
                                    )
                                }

                                /*
                                 * Find the main detected object.
                                 */
                                val primaryObject =
                                    result.primaryObject

                                /*
                                 * HAPTIC FEEDBACK
                                 */
                                if (primaryObject != null) {

                                    val riskLevel =
                                        primaryObject
                                            .trust
                                            .riskLevel

                                    hapticManager.triggerHapticForRisk(

                                        riskLevel = riskLevel,

                                        isEnabled =
                                            appSettings.hapticEnabled,

                                        intensity =
                                            appSettings.hapticIntensity
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
                            "Camera error: ${
                                error.localizedMessage
                                    ?: "Unknown error"
                            }",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )

        /*
         * ==============================
         * DETECTION OVERLAY
         * ==============================
         */
        if (
            appSettings.showDetectionBoxes &&
            sceneResult != null
        ) {

            val objects =
                buildList {

                    sceneResult
                        ?.primaryObject
                        ?.let { objectItem ->
                            add(objectItem)
                        }

                    sceneResult
                        ?.secondaryObjects
                        ?.let { secondaryObjects ->
                            addAll(secondaryObjects)
                        }
                }

            DetectionOverlay(
                trackedObjects = objects,
                imageWidth = frameWidth,
                imageHeight = frameHeight
            )
        }

        /*
         * ==============================
         * STATUS
         * ==============================
         */
        val primaryObject =
            sceneResult?.primaryObject

        val currentTrustLevel =
            primaryObject
                ?.trust
                ?.trustLevel
                ?: TrustLevel.HIGH

        val currentRiskLevel =
            primaryObject
                ?.trust
                ?.riskLevel
                ?: RiskLevel.SAFE

        val currentStatusMessage =
            ocrText
                ?: sceneResult
                    ?.fullSceneDescription
                ?: "Analyzing environment..."

        StatusIndicator(

            isActive = isAnalyzing,

            statusMessage = currentStatusMessage,

            trustLevel = currentTrustLevel,

            riskLevel = currentRiskLevel,

            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        /*
         * ==============================
         * BOTTOM CONTROLS
         * ==============================
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
             * ==============================
             * OCR BUTTON
             * ==============================
             *
             * The actual OCR processing can be
             * connected later through AIEngine.ocrProcessor.
             */
            SecondaryActionButton(

                text = "READ THIS TEXT (OCR)",

                icon = Icons.Default.TextFields,

                onClick = {

                    ocrText = null

                    voiceManager.speakUrgent(
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
             * ==============================
             * STOP ASSISTANCE
             * ==============================
             */
            PrimaryActionButton(

                text = "STOP ASSISTANCE",

                icon = Icons.Default.Stop,

                onClick = {

                    /*
                     * Stop analysis.
                     */
                    isAnalyzing = false

                    /*
                     * Stop voice.
                     */
                    voiceManager.stop()

                    /*
                     * Stop vibration.
                     */
                    hapticManager.stop()

                    /*
                     * Stop camera.
                     */
                    cameraManager.stopCamera()

                    /*
                     * Return to previous screen.
                     */
                    onStopAssistance()
                },

                isDanger = true
            )
        }
    }
}