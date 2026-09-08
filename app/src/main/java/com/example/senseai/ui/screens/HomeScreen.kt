package com.example.senseai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.senseai.ui.components.PrimaryActionButton
import com.example.senseai.ui.components.SecondaryActionButton
import com.example.senseai.utils.Constants

@Composable
fun HomeScreen(
    onStartSenseAI: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(28.dp)
            .semantics {
                contentDescription =
                    "SenseAI home screen. " +
                            "Assistive vision system."
            },

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        /*
         * App name
         */
        Text(
            text = Constants.APP_NAME,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * Tagline
         */
        Text(
            text = Constants.TAGLINE,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.75f)
        )

        Spacer(
            modifier = Modifier.height(36.dp)
        )

        /*
         * Description
         */
        Text(
            text =
                "An AI-powered assistant that " +
                        "helps you understand your surroundings " +
                        "using your phone camera, voice and haptic feedback.",

            modifier = Modifier.fillMaxWidth(),

            textAlign = TextAlign.Center,

            fontSize = 17.sp,

            lineHeight = 25.sp,

            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.8f)
        )

        Spacer(
            modifier = Modifier.height(42.dp)
        )

        /*
         * Start button
         */
        PrimaryActionButton(
            text = "START SENSEAI",
            icon = Icons.Default.CameraAlt,
            onClick = onStartSenseAI
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        /*
         * Settings button
         */
        SecondaryActionButton(
            text = "SETTINGS",
            icon = Icons.Default.Settings,
            onClick = onOpenSettings
        )

        Spacer(
            modifier = Modifier.height(36.dp)
        )

        /*
         * Feature summary
         */
        Text(
            text =
                "AI Vision  •  Voice Guidance  •  " +
                        "Distance  •  Movement  •  Safety Alerts",

            modifier = Modifier.fillMaxWidth(),

            textAlign = TextAlign.Center,

            fontSize = 13.sp,

            color =
                MaterialTheme.colorScheme
                    .onBackground
                    .copy(alpha = 0.6f)
        )
    }
}