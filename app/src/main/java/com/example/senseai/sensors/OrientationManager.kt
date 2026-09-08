package com.example.senseai.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import com.example.senseai.utils.Logger

data class OrientationData(
    val pitchDegrees: Float = 0f,
    val rollDegrees: Float = 0f,
    val isFacingForward: Boolean = true
)

class OrientationManager(
    context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as? AndroidSensorManager

    private val accelerometer =
        sensorManager?.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

    private val magnetometer =
        sensorManager?.getDefaultSensor(
            Sensor.TYPE_MAGNETIC_FIELD
        )

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    private var hasGravity = false
    private var hasGeomagnetic = false

    var currentOrientation = OrientationData()
        private set

    fun start() {
        val manager = sensorManager ?: return

        accelerometer?.let {
            manager.registerListener(
                this,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }

        magnetometer?.let {
            manager.registerListener(
                this,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop() {
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            Logger.e(
                "Error stopping orientation sensors",
                e
            )
        }
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {
        if (event == null) return

        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values,
                    0,
                    gravity,
                    0,
                    3
                )
                hasGravity = true
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(
                    event.values,
                    0,
                    geomagnetic,
                    0,
                    3
                )
                hasGeomagnetic = true
            }
        }

        if (!hasGravity || !hasGeomagnetic) {
            return
        }

        val rotationMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)

        val rotationValid =
            AndroidSensorManager.getRotationMatrix(
                rotationMatrix,
                inclinationMatrix,
                gravity,
                geomagnetic
            )

        if (!rotationValid) {
            return
        }

        val orientation = FloatArray(3)

        AndroidSensorManager.getOrientation(
            rotationMatrix,
            orientation
        )

        val pitch =
            Math.toDegrees(
                orientation[1].toDouble()
            ).toFloat()

        val roll =
            Math.toDegrees(
                orientation[2].toDouble()
            ).toFloat()

        val isFacingForward =
            pitch in -140f..-40f

        currentOrientation =
            OrientationData(
                pitchDegrees = pitch,
                rollDegrees = roll,
                isFacingForward = isFacingForward
            )
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // No action required.
    }
}