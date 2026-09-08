package com.example.senseai.ai

import android.graphics.RectF
import com.example.senseai.data.model.DetectionResult
import com.example.senseai.utils.Constants
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

data class TrackedHistory(
    val trackingId: Int,
    val className: String,
    val historyBoxes: MutableList<Pair<Long, RectF>> = mutableListOf(),
    val historyConfidences: MutableList<Float> = mutableListOf(),
    var firstSeenTimestamp: Long = System.currentTimeMillis(),
    var lastSeenTimestamp: Long = System.currentTimeMillis(),
    var detectionCount: Int = 1
)

class ObjectTracker {

    companion object {
        private const val TRACK_TIMEOUT_MS = 2500L
        private const val MIN_IOU = 0.30f
        private const val MAX_CENTER_DISTANCE_RATIO = 1.5f
    }

    private val activeTracks =
        ConcurrentHashMap<Int, TrackedHistory>()

    private var nextSyntheticId = 1000

    fun processDetections(
        detections: List<DetectionResult>
    ): Map<Int, TrackedHistory> {

        val now = System.currentTimeMillis()

        val validDetections = detections.filter {
            it.boundingBox.width() > 0f &&
                    it.boundingBox.height() > 0f
        }

        for (detection in validDetections) {

            val trackId =
                detection.trackingId
                    ?: findMatchingTrackId(
                        detection.className,
                        detection.boundingBox
                    )
                    ?: nextSyntheticId++

            val history = activeTracks.getOrPut(trackId) {
                TrackedHistory(
                    trackingId = trackId,
                    className = detection.className,
                    firstSeenTimestamp = now
                )
            }

            history.lastSeenTimestamp = now
            history.detectionCount++

            history.historyBoxes.add(
                Pair(now, RectF(detection.boundingBox))
            )

            history.historyConfidences.add(
                detection.confidence.coerceIn(0f, 1f)
            )

            while (
                history.historyBoxes.size >
                Constants.MAX_TRACKING_HISTORY
            ) {
                history.historyBoxes.removeAt(0)
            }

            while (
                history.historyConfidences.size >
                Constants.MAX_TRACKING_HISTORY
            ) {
                history.historyConfidences.removeAt(0)
            }
        }

        removeExpiredTracks(now)

        return activeTracks.toMap()
    }

    private fun findMatchingTrackId(
        className: String,
        box: RectF
    ): Int? {

        var bestId: Int? = null
        var bestScore = 0f

        for ((id, track) in activeTracks) {

            if (
                !track.className.equals(
                    className,
                    ignoreCase = true
                )
            ) {
                continue
            }

            val lastBox =
                track.historyBoxes.lastOrNull()?.second
                    ?: continue

            val iou = calculateIoU(box, lastBox)

            val centerDistance =
                calculateCenterDistance(box, lastBox)

            val referenceSize =
                max(
                    (box.width() + box.height()) / 2f,
                    1f
                )

            val normalizedCenterDistance =
                centerDistance / referenceSize

            val centerCompatible =
                normalizedCenterDistance <=
                        MAX_CENTER_DISTANCE_RATIO

            if (!centerCompatible && iou < MIN_IOU) {
                continue
            }

            val score =
                if (iou >= MIN_IOU) {
                    iou
                } else {
                    1f /
                            (1f + normalizedCenterDistance)
                }

            if (score > bestScore) {
                bestScore = score
                bestId = id
            }
        }

        return bestId
    }

    private fun calculateIoU(
        a: RectF,
        b: RectF
    ): Float {

        val left = max(a.left, b.left)
        val top = max(a.top, b.top)
        val right = min(a.right, b.right)
        val bottom = min(a.bottom, b.bottom)

        if (right <= left || bottom <= top) {
            return 0f
        }

        val intersectionArea =
            (right - left) * (bottom - top)

        val areaA =
            a.width() * a.height()

        val areaB =
            b.width() * b.height()

        val unionArea =
            areaA + areaB - intersectionArea

        return if (unionArea > 0f) {
            intersectionArea / unionArea
        } else {
            0f
        }
    }

    private fun calculateCenterDistance(
        a: RectF,
        b: RectF
    ): Float {

        val dx =
            a.centerX() - b.centerX()

        val dy =
            a.centerY() - b.centerY()

        return kotlin.math.hypot(dx, dy)
    }

    private fun removeExpiredTracks(
        currentTime: Long
    ) {
        activeTracks.entries.removeIf { (_, track) ->
            currentTime - track.lastSeenTimestamp >
                    TRACK_TIMEOUT_MS
        }
    }

    fun getTrack(
        trackingId: Int
    ): TrackedHistory? {
        return activeTracks[trackingId]
    }

    fun getActiveTracks(): Map<Int, TrackedHistory> {
        return activeTracks.toMap()
    }

    fun clear() {
        activeTracks.clear()
        nextSyntheticId = 1000
    }
}