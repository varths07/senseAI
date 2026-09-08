package com.example.senseai.ui.components

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    onPreviewViewCreated: (PreviewView) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->

            PreviewView(context).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                /*
                 * Keep the camera preview filling
                 * the complete screen.
                 */
                scaleType =
                    PreviewView.ScaleType.FILL_CENTER

                implementationMode =
                    PreviewView.ImplementationMode.PERFORMANCE

                /*
                 * Important for accessibility:
                 * the camera preview itself does not
                 * need to receive accessibility focus.
                 */
                isFocusable = false
                importantForAccessibility =
                    android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO

                onPreviewViewCreated(this)
            }
        },

        modifier = modifier.fillMaxSize(),

        update = { previewView ->

            /*
             * Make sure the preview remains visible
             * when Compose recomposes.
             */
            previewView.visibility =
                android.view.View.VISIBLE
        }
    )
}