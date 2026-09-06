package com.example.senseai.ai

import android.graphics.RectF
import com.example.senseai.data.model.DetectionResult
import com.example.senseai.utils.Constants
import java.util.concurrent.ConcurrentHashMap

data class TrackedHistory(
    val trackingId: Int,
    val className: String,
    val historyBoxes: MutableList<Pair<Long, RectF>> = mutableListOf(),
    var firstSeenTimestamp: Long = System.currentTimeMillis(),
    var lastSeenTimestamp: Long = System.currentTimeMillis(),
    var detectionCount: Int = 1
)

class ObjectTracker {
    private val activeTracks = ConcurrentHashMap<Int, TrackedHistory>()
    private var nextSyntheticId = 1000

    fun processDetections(detections: List<DetectionResult>): Map<Int, TrackedHistory> {
        val now = System.currentTimeMillis()

        for (det in detections) {
            val trackId = det.trackingId ?: findMatchingTrackId(det.boundingBox) ?: nextSyntheticId++

            val history = activeTracks.getOrPut(trackId) {
                TrackedHistory(
                    trackingId = trackId,
                    className = det.className,
                    firstSeenTimestamp = now
                )
            }

            history.lastSeenTimestamp = now
            history.detectionCount++
            history.historyBoxes.add(Pair(now, det.boundingBox))

            // Keep history bounded
            if (history.historyBoxes.size > Constants.MAX_TRACKING_HISTORY) {
                history.historyBoxes.removeAt(0)
            }
        }

        // Clean up stale tracks older than 2.5 seconds
        activeTracks.entries.removeIf { (_, track) ->
            now - track.lastSeenTimestamp > 2500L
        }

        return activeTracks
    }

    private fun findMatchingTrackId(box: RectF): Int? {
        var bestId: Int? = null
        var maxIoU = 0.35f // Minimum IoU overlap threshold

        for ((id, track) in activeTracks) {
            val lastBox = track.historyBoxes.lastOrNull()?.second ?: continue
            val iou = calculateIoU(box, lastBox)
            if (iou > maxIoU) {
                maxIoU = iou
                bestId = id
            }
        }

        return bestId
    }

    private fun calculateIoU(a: RectF, b: RectF): Float {
        val intersectionLeft = maxOf(a.left, b.left)
        val intersectionTop = maxOf(a.top, b.top)
        val intersectionRight = minOf(a.right, b.right)
        val intersectionBottom = minOf(a.bottom, b.bottom)

        if (intersectionRight < intersectionLeft || intersectionBottom < intersectionTop) {
            return 0.0f
        }

        val intersectionArea = (intersectionRight - intersectionLeft) * (intersectionBottom - intersectionTop)
        val areaA = a.width() * a.height()
        val areaB = b.width() * b.height()
        val unionArea = areaA + areaB - intersectionArea

        return if (unionArea > 0f) intersectionArea / unionArea else 0.0f
    }

    fun clear() {
        activeTracks.clear()
    }
}
