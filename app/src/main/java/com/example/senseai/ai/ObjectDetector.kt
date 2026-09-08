package com.example.senseai.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import com.example.senseai.data.model.DetectionResult
import com.example.senseai.utils.Logger
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

class ObjectDetector {

    companion object {
        private const val DEFAULT_CONFIDENCE_THRESHOLD = 0.15f
        private const val DEFAULT_IOU_THRESHOLD = 0.45f
        private const val NUM_THREADS = 4

        private val CLASS_NAMES = listOf(
            "organic", "plastic", "metal", "glass", "paper", "person"
        )
    }

    private var interpreter: Interpreter? = null
    private var inputWidth = 224
    private var inputHeight = 224
    private var inputType = DataType.UINT8
    private var outputType = DataType.UINT8
    private var outputShape = IntArray(0)
    private var initialized = false

    fun isInitialized() = initialized

    fun initialize(modelFile: File): Boolean {
        return try {
            interpreter?.close()
            val options = Interpreter.Options().setNumThreads(NUM_THREADS)
            val newInterpreter = Interpreter(modelFile, options)
            interpreter = newInterpreter

            val inputTensor = newInterpreter.getInputTensor(0)
            val inputShape = inputTensor.shape()
            inputType = inputTensor.dataType()
            inputHeight = if (inputShape.size >= 4) inputShape[1] else 224
            inputWidth = if (inputShape.size >= 4) inputShape[2] else 224

            val outputTensor = newInterpreter.getOutputTensor(0)
            outputShape = outputTensor.shape()
            outputType = outputTensor.dataType()

            initialized = true
            Logger.i("ObjectDetector: Initialized with input ${inputWidth}x${inputHeight}, output ${outputShape.contentToString()}")
            true
        } catch (e: Exception) {
            Logger.e("ObjectDetector: Init failed", e)
            false
        }
    }

    fun detectObjects(
        imageProxy: ImageProxy,
        onSuccess: (List<DetectionResult>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            imageProxy.close()

            if (bitmap == null) {
                onSuccess(emptyList())
                return
            }

            val results = detectBitmap(bitmap)
            onSuccess(results)
        } catch (e: Exception) {
            imageProxy.close()
            onError(e)
        }
    }

    private fun detectBitmap(bitmap: Bitmap): List<DetectionResult> {
        val currentInterpreter = interpreter ?: return emptyList()
        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        
        val inputBuffer = createInputBuffer(resized)
        val outputBufferSize = outputShape.fold(1) { a, b -> a * b } * bytesPerValue(outputType)
        val outputBuffer = ByteBuffer.allocateDirect(outputBufferSize).order(ByteOrder.nativeOrder())

        currentInterpreter.run(inputBuffer, outputBuffer)
        
        val results = decodeOutput(outputBuffer, bitmap.width, bitmap.height)
        resized.recycle()
        return nonMaximumSuppression(results, DEFAULT_IOU_THRESHOLD)
    }

    private fun createInputBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(inputWidth * inputHeight * 3 * bytesPerValue(inputType))
            .order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputWidth * inputHeight)
        bitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            if (inputType == DataType.UINT8) {
                buffer.put(r.toByte())
                buffer.put(g.toByte())
                buffer.put(b.toByte())
            } else {
                buffer.putFloat(r / 255f)
                buffer.putFloat(g / 255f)
                buffer.putFloat(b / 255f)
            }
        }
        return buffer
    }

    private fun decodeOutput(buffer: ByteBuffer, width: Int, height: Int): List<DetectionResult> {
        val values = readOutputValues(buffer)
        if (values.isEmpty()) return emptyList()

        // Classification path [1, N]
        if (outputShape.size == 2 && outputShape[0] == 1) {
            var bestClass = -1
            var bestScore = 0f
            for (i in values.indices) {
                val score = if (outputType == DataType.UINT8) values[i] / 255f else values[i]
                if (score > bestScore) {
                    bestScore = score
                    bestClass = i
                }
            }
            if (bestClass < 0 || bestScore < DEFAULT_CONFIDENCE_THRESHOLD) return emptyList()
            
            val name = if (bestClass in CLASS_NAMES.indices) CLASS_NAMES[bestClass] else "object_$bestClass"
            Logger.d("ObjectDetector: Detected $name ($bestScore)")
            return listOf(DetectionResult(name, bestScore, RectF(width*0.1f, height*0.1f, width*0.9f, height*0.9f)))
        }

        // Simple YOLO path [1, N, 4+C]
        if (outputShape.size == 3) {
            val detections = mutableListOf<DetectionResult>()
            val rows = outputShape[1]
            val cols = outputShape[2]
            // Implement simple decoding logic here if needed
            return detections
        }

        return emptyList()
    }

    private fun readOutputValues(buffer: ByteBuffer): FloatArray {
        buffer.rewind()
        val size = outputShape.fold(1) { a, b -> a * b }
        val values = FloatArray(size)
        for (i in 0 until size) {
            values[i] = if (outputType == DataType.UINT8) (buffer.get().toInt() and 0xFF).toFloat() else buffer.float
        }
        return values
    }

    private fun nonMaximumSuppression(detections: List<DetectionResult>, threshold: Float): List<DetectionResult> {
        return detections // Simplification for now
    }

    private fun bytesPerValue(type: DataType) = when(type) {
        DataType.UINT8, DataType.INT8 -> 1
        else -> 4
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 90, out)
        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        initialized = false
    }
}
