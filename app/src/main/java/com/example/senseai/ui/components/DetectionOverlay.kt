package com.example.senseai.ui.components

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.senseai.data.model.MovementState
import com.example.senseai.data.model.RiskLevel
import com.example.senseai.data.model.TrackedObject
import com.example.senseai.ui.theme.AlertAmber
import com.example.senseai.ui.theme.BoundingBoxColor
import com.example.senseai.ui.theme.DangerRed

@Composable
fun DetectionOverlay(
    trackedObjects: List<TrackedObject>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        if (
            imageWidth <= 0 ||
            imageHeight <= 0 ||
            trackedObjects.isEmpty()
        ) {
            return@Canvas
        }

        val scaleX =
            size.width / imageWidth.toFloat()

        val scaleY =
            size.height / imageHeight.toFloat()

        trackedObjects.forEach { trackedObject ->

            val originalBox =
                trackedObject.detection.boundingBox

            if (
                originalBox.width() <= 0f ||
                originalBox.height() <= 0f
            ) {
                return@forEach
            }

            val box = RectF(
                originalBox.left * scaleX,
                originalBox.top * scaleY,
                originalBox.right * scaleX,
                originalBox.bottom * scaleY
            )

            val risk =
                trackedObject.trust.riskLevel

            val boxColor =
                when (risk) {
                    RiskLevel.HIGH ->
                        DangerRed

                    RiskLevel.MEDIUM ->
                        AlertAmber

                    else ->
                        BoundingBoxColor
                }

            /*
             * Bounding box
             */
            drawRect(
                color = boxColor,
                topLeft = Offset(
                    box.left,
                    box.top
                ),
                size = Size(
                    box.width(),
                    box.height()
                ),
                style = Stroke(
                    width = 5f
                )
            )

            /*
             * Object name
             */
            val objectName =
                trackedObject
                    .detection
                    .className
                    .trim()
                    .replaceFirstChar {
                        it.uppercase()
                    }

            /*
             * Direction
             */
            val direction =
                trackedObject
                    .spatial
                    .direction
                    .label

            /*
             * Distance
             */
            val distance =
                trackedObject
                    .spatial
                    .distanceFormatted

            /*
             * Confidence
             */
            val confidence =
                (
                        trackedObject
                            .detection
                            .confidence
                            .coerceIn(0f, 1f) * 100
                        ).toInt()

            /*
             * Movement
             */
            val approaching =
                trackedObject
                    .spatial
                    .movementState ==
                        MovementState.APPROACHING

            val movementText =
                if (approaching) {
                    " • APPROACHING"
                } else {
                    ""
                }

            val labelText =
                "$objectName • " +
                        "${direction.uppercase()} • " +
                        "$distance$movementText • " +
                        "$confidence%"

            /*
             * Paint for label.
             */
            val textPaint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color =
                        android.graphics.Color.WHITE

                    textSize = 34f

                    isFakeBoldText = true

                    setShadowLayer(
                        5f,
                        2f,
                        2f,
                        android.graphics.Color.BLACK
                    )
                }

            /*
             * Background paint.
             */
            val backgroundPaint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color =
                        boxColor.toArgb()

                    style =
                        Paint.Style.FILL
                }

            val paddingHorizontal = 14f
            val paddingVertical = 10f

            val textWidth =
                textPaint.measureText(labelText)

            val bannerWidth =
                textWidth +
                        paddingHorizontal * 2

            val bannerHeight = 58f

            /*
             * Put label above the box.
             * If there isn't enough room,
             * put it inside the box.
             */
            val labelTop =
                if (box.top >= bannerHeight) {
                    box.top - bannerHeight
                } else {
                    box.top
                }

            val labelLeft =
                box.left.coerceIn(
                    0f,
                    (size.width - bannerWidth)
                        .coerceAtLeast(0f)
                )

            /*
             * Label background.
             */
            drawContext
                .canvas
                .nativeCanvas
                .drawRoundRect(
                    labelLeft,
                    labelTop,
                    labelLeft + bannerWidth,
                    labelTop + bannerHeight,
                    10f,
                    10f,
                    backgroundPaint
                )

            /*
             * Label text.
             */
            drawContext
                .canvas
                .nativeCanvas
                .drawText(
                    labelText,
                    labelLeft + paddingHorizontal,
                    labelTop +
                            bannerHeight -
                            paddingVertical,
                    textPaint
                )
        }
    }
}