package com.example.senseai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.TrustLevel
import com.example.senseai.ui.theme.AlertAmber
import com.example.senseai.ui.theme.DangerRed
import com.example.senseai.ui.theme.SecondaryGreen

@Composable
fun StatusIndicator(
    isActive: Boolean,
    statusMessage: String,
    trustLevel: TrustLevel = TrustLevel.HIGH,
    riskLevel: RiskLevel = RiskLevel.SAFE,
    modifier: Modifier = Modifier
) {

    val indicatorColor = when {
        !isActive -> Color.Gray
        riskLevel == RiskLevel.HIGH -> DangerRed
        riskLevel == RiskLevel.MEDIUM -> AlertAmber
        else -> SecondaryGreen
    }

    val riskText = when (riskLevel) {
        RiskLevel.HIGH -> "HIGH RISK"
        RiskLevel.MEDIUM -> "MEDIUM RISK"
        RiskLevel.LOW -> "LOW RISK"
        RiskLevel.SAFE -> "SAFE"
    }

    val trustText = when (trustLevel) {
        TrustLevel.HIGH -> "HIGH"
        TrustLevel.MEDIUM -> "MEDIUM"
        TrustLevel.LOW -> "LOW"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .semantics {
                contentDescription =
                    "SenseAI status. " +
                            "$statusMessage. " +
                            "Risk: $riskText. " +
                            "Trust: $trustText."
            },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * Live status indicator
             */
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        color = indicatorColor,
                        shape = CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text =
                        if (isActive) {
                            "SenseAI ACTIVE"
                        } else {
                            "SenseAI STANDBY"
                        },

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = statusMessage.ifBlank {
                        "Analyzing environment..."
                    },

                    style =
                        MaterialTheme.typography.bodyMedium,

                    fontSize = 14.sp,

                    maxLines = 2
                )
            }

            /*
             * Risk indicator
             */
            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text = riskText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = indicatorColor
                )

                Text(
                    text = "Trust $trustText",
                    fontSize = 11.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}