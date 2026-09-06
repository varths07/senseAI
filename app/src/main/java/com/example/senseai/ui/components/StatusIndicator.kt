package com.example.senseai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                contentDescription = "Status: $statusMessage. Risk level: ${riskLevel.name}"
            },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(indicatorColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isActive) "SenseAI Active" else "SenseAI Standby",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}
