package com.example.senseai.spatial

import android.graphics.RectF
import com.example.senseai.data.model.SpatialDirection

class DirectionAnalyzer {

    fun calculateDirection(
        boundingBox: RectF,
        imageWidth: Float
    ): SpatialDirection {
        if (imageWidth <= 0f) return SpatialDirection.CENTER

        val centerX = boundingBox.centerX()
        val normalizedX = centerX / imageWidth // 0.0 (left) to 1.0 (right)

        return when {
            normalizedX < 0.15f -> SpatialDirection.FAR_LEFT
            normalizedX < 0.33f -> SpatialDirection.LEFT
            normalizedX < 0.42f -> SpatialDirection.SLIGHTLY_LEFT
            normalizedX <= 0.58f -> SpatialDirection.CENTER
            normalizedX < 0.67f -> SpatialDirection.SLIGHTLY_RIGHT
            normalizedX < 0.85f -> SpatialDirection.RIGHT
            else -> SpatialDirection.FAR_RIGHT
        }
    }
}
