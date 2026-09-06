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

class OrientationManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? AndroidSensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    var currentOrientation = OrientationData()
        private set

    fun start() {
        if (sensorManager == null) return
        accelerometer?.let { sensorManager.registerListener(this, it, AndroidSensorManager.SENSOR_DELAY_NORMAL) }
        magnetometer?.let { sensorManager.registerListener(this, it, AndroidSensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun stop() {
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            Logger.e("Error stopping orientation sensors", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, 3)
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
            hasGeomagnetic = true
        }

        if (hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (AndroidSensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                AndroidSensorManager.getOrientation(r, orientation)

                val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

                // Facing forward if held vertically (-45 deg to -135 deg pitch)
                val isFacingForward = pitch in -140.0f..-40.0f

                currentOrientation = OrientationData(
                    pitchDegrees = pitch,
                    rollDegrees = roll,
                    isFacingForward = isFacingForward
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
