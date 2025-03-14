package com.example.uitutorial.navigationalComponents

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uitutorial.viewModels.CameraMediapipeViewModel
import com.example.uitutorial.viewModels.CameraPermissionViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

@Composable
fun PoseCheck(viewPermissionModel: CameraPermissionViewModel = viewModel()) {
    object {
        init {
            System.loadLibrary("mediapipe_tasks_vision_jni")
        }
    }
    val hasPermission by viewPermissionModel.hasPermission.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val currentContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var poseResult by remember { mutableStateOf<PoseLandmarkerResult?>(null) }
    var processingImage by remember { mutableStateOf(false) }

    val poseLandmarker = remember {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker_lite.task")
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumPoses(1)
                .setMinPoseDetectionConfidence(0.3f)
                .setMinPosePresenceConfidence(0.3f)
                .setMinTrackingConfidence(0.3f)
                .build()

            PoseLandmarker.createFromOptions(currentContext, options)
        } catch (e: Exception) {
            Log.e("PoseDetection", "Error creating pose landmarker", e)
            null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewPermissionModel.updatePermissionState()
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        val controller = remember {
            LifecycleCameraController(currentContext).apply {
                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            }
        }

        val viewModel = viewModel<CameraMediapipeViewModel>()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

            if (poseResult != null) {
                PoseLandmarkOverlay(poseResult = poseResult!!)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                }) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (!processingImage) {
                            processingImage = true
                            takePhotoAndDetectPose(
                                controller = controller,
                                context = currentContext,
                                poseLandmarker = poseLandmarker,
                                onPhotoTaken = viewModel::onTakePhoto,
                                onPoseDetected = { result ->
                                    poseResult = result
                                    processingImage = false
                                },
                                onError = {
                                    processingImage = false
                                    Log.e("PoseDetection", "Error processing image", it)
                                }
                            )
                        }
                    },
                    enabled = !processingImage
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take Picture",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

fun takePhotoAndDetectPose(
    controller: LifecycleCameraController,
    context: Context,
    poseLandmarker: PoseLandmarker?,
    onPhotoTaken: (Bitmap) -> Unit,
    onPoseDetected: (PoseLandmarkerResult) -> Unit,
    onError: (Exception) -> Unit
) {
    if (poseLandmarker == null) {
        onError(Exception("PoseLandmarker is not initialized"))
        return
    }

    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.toBitmap()
                onPhotoTaken(bitmap)

                try {
                    val mpImage = BitmapImageBuilder(bitmap).build()
                    val result = poseLandmarker.detect(mpImage)
                    onPoseDetected(result)
                } catch (e: Exception) {
                    onError(e)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onError(exception)
                Log.e("Camera", "Could not take image", exception)
            }
        }
    )
}

@Composable
fun PoseLandmarkOverlay(poseResult: PoseLandmarkerResult) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (poseResult.landmarks().isNotEmpty()) {
            val landmarks = poseResult.landmarks()[0]

            val connectionPairs = listOf(
                // Torso
                Pair(11, 12), // Left shoulder to right shoulder
                Pair(11, 23), // Left shoulder to left hip
                Pair(12, 24), // Right shoulder to right hip
                Pair(23, 24), // Left hip to right hip

                // Arms
                Pair(11, 13), // Left shoulder to left elbow
                Pair(13, 15), // Left elbow to left wrist
                Pair(12, 14), // Right shoulder to right elbow
                Pair(14, 16), // Right elbow to right wrist

                // Legs
                Pair(23, 25), // Left hip to left knee
                Pair(25, 27), // Left knee to left ankle
                Pair(24, 26), // Right hip to right knee
                Pair(26, 28), // Right knee to right ankle
            )

            // Draw connections
            for (pair in connectionPairs) {
                if (pair.first < landmarks.size && pair.second < landmarks.size) {
                    val startLandmark = landmarks[pair.first]
                    val endLandmark = landmarks[pair.second]


                    try {
                        val startX = startLandmark.x() * canvasWidth
                        val startY = startLandmark.y() * canvasHeight
                        val endX = endLandmark.x() * canvasWidth
                        val endY = endLandmark.y() * canvasHeight

                        if (startX in 0f..canvasWidth && startY in 0f..canvasHeight &&
                            endX in 0f..canvasWidth && endY in 0f..canvasHeight) {
                            drawLine(
                                color = Color.Green,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 5f
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("PoseDetection", "Error drawing connection: ${e.message}")
                    }
                }
            }

            for (i in landmarks.indices) {
                try {
                    val landmark = landmarks[i]
                    val x = landmark.x() * canvasWidth
                    val y = landmark.y() * canvasHeight

                    if (x in 0f..canvasWidth && y in 0f..canvasHeight) {
                        drawCircle(
                            color = Color.Red,
                            radius = 8f,
                            center = Offset(x, y)
                        )
                    }
                } catch (e: Exception) {
                    Log.d("PoseDetection", "Error drawing landmark: ${e.message}")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
){
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}