package com.example.senseai.ui.components

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.senseai.data.model.MovementState
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.TrackedObject
import com.example.senseai.ui.theme.AlertAmber
import com.example.senseai.ui.theme.BoundingBoxColor
import com.example.senseai.ui.theme.DangerRed
import com.example.senseai.ui.theme.SecondaryGreen

@Composable
fun DetectionOverlay(
    trackedObjects: List<TrackedObject>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (imageWidth <= 0 || imageHeight <= 0) return@Canvas

        val scaleX = size.width / imageWidth.toFloat()
        val scaleY = size.height / imageHeight.toFloat()

        for (obj in trackedObjects) {
            val box = obj.detection.boundingBox
            val scaledBox = RectF(
                box.left * scaleX,
                box.top * scaleY,
                box.right * scaleX,
                box.bottom * scaleY
            )

            val boxColor = when (obj.trust.riskLevel) {
                RiskLevel.HIGH -> DangerRed
                RiskLevel.MEDIUM -> AlertAmber
                else -> BoundingBoxColor
            }

            // Draw bounding box outline
            drawRect(
                color = boxColor,
                topLeft = androidx.compose.ui.geometry.Offset(scaledBox.left, scaledBox.top),
                size = androidx.compose.ui.geometry.Size(scaledBox.width(), scaledBox.height()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
            )

            // Prepare paint for overlay text card
            val labelText = buildString {
                append(obj.detection.className.replaceFirstChar { it.uppercase() })
                append(" | ${obj.spatial.direction.label.uppercase()}")
                append(" | ${obj.spatial.distanceFormatted}")
                if (obj.spatial.movementState == MovementState.APPROACHING) {
                    append(" | APPROACHING")
                }
                append(" (${(obj.detection.confidence * 100).toInt()}%)")
            }

            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 36f
                isFakeBoldText = true
                setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
            }

            val bgPaint = Paint().apply {
                color = boxColor.toArgb()
                style = Paint.Style.FILL
            }

            val textWidth = paint.measureText(labelText)
            val textHeight = 44f
            val textTop = (scaledBox.top - textHeight).coerceAtLeast(0f)

            // Background banner for label
            drawContext.canvas.nativeCanvas.drawRect(
                scaledBox.left,
                textTop,
                scaledBox.left + textWidth + 24f,
                textTop + textHeight + 12f,
                bgPaint
            )

            // Text
            drawContext.canvas.nativeCanvas.drawText(
                labelText,
                scaledBox.left + 12f,
                textTop + textHeight,
                paint
            )
        }
    }
}
