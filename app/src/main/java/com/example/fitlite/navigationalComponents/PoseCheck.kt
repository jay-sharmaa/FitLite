package com.example.fitlite.navigationalComponents

import android.Manifest
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
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
import com.example.fitlite.viewModels.CameraMediapipeViewModel
import com.example.fitlite.viewModels.CameraPermissionViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.fitlite.data.extractPoseAngles2D
import com.example.fitlite.data.getPushUpAngleChecks
import com.example.fitlite.data.getSquatAngleChecks
import com.example.fitlite.myList
import java.util.Locale

private const val TAG = "VOICE_FEED_BACK"

@Composable
fun PoseCheck(
    viewPermissionModel: CameraPermissionViewModel = viewModel(),
    modifier: Modifier,
    navController: NavHostController,
    dataId: String
) {
    object {
        init {
            System.loadLibrary("mediapipe_tasks_vision_jni")
        }
    }

    val hasPermission by viewPermissionModel.hasPermission.collectAsState(initial = false)
    val currentContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isListening by remember { mutableStateOf(false) }

    var poseResult by remember { mutableStateOf<PoseLandmarkerResult?>(null) }
    var processingImage by remember { mutableStateOf(false) }

    val contextvoice = LocalContext.current
    var permissionGrantedvoice by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                contextvoice,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

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

    val controller = remember {
        LifecycleCameraController(currentContext).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    DisposableEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose {
            controller.unbind()
        }
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(contextvoice) }
    var text by remember { mutableStateOf("") }
    var textCounter by remember { mutableIntStateOf(0) }

    val permissionLaunchervoice = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGrantedvoice = isGranted
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
            Log.d("SpeechRecognizer", "Stopped and destroyed")
        }
    }

    LaunchedEffect(Unit) {
        controller.bindToLifecycle(lifecycleOwner)
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
        permissionLaunchervoice.launch(Manifest.permission.RECORD_AUDIO)
        if (permissionGrantedvoice) {
            Toast.makeText(contextvoice, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(contextvoice, "Give Permission", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(isListening, text) {
        if (isListening) {
            if (!permissionGrantedvoice) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                }

                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        val spokenText =
                            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull() ?: ""
                        text = spokenText
                        Toast.makeText(currentContext, spokenText, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: Int) {
                        text = "" + textCounter++
                        Toast.makeText(currentContext, error.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer.startListening(intent)
            }

            if (text.equals("skip", true) || text.equals("escape", true) || text.equals(
                    "scape",
                    true
                )
            ) {
                navController.popBackStack()
            }
        }
    }

    val viewModel = viewModel<CameraMediapipeViewModel>()
    val lifecycleOwnerCamera = LocalLifecycleOwner.current

    LaunchedEffect(hasPermission) {
        lifecycleOwnerCamera.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (hasPermission) {
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
                kotlinx.coroutines.delay(3000)
            }
        }
    }

    if (hasPermission) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val cameraFacing = viewModel.cameraFacing
            CameraPreview(controller = controller, modifier = modifier)
            poseResult?.let {
                PoseLandmarkOverlay(poseResult = it, dataId = dataId)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = {
                    viewModel.toggleCameraFacing()
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
                IconButton(onClick = {
                    if (isListening) {
                        isListening = false
                    } else {
                        isListening = true
                        Toast.makeText(currentContext, "Started Listening", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = if (isListening) "Stop Listening" else "Start Listening",
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
    onError: (Exception) -> Unit,
) {
    if (poseLandmarker == null) {
        onError(Exception("PoseLandmarker is not initialized"))
        return
    }

    if (controller.cameraInfo == null) {
        onError(Exception("Camera is not ready or has been closed"))
        return
    }

    try {
        controller.takePicture(
            ContextCompat.getMainExecutor(context),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = image.toBitmap()
                    image.close()

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
    } catch (e: IllegalStateException) {
        onError(Exception("Camera not available: ${e.message}"))
        Log.e("Camera", "Attempted to capture while camera is not active", e)
    }
}

@Composable
fun PoseLandmarkOverlay(
    poseResult: PoseLandmarkerResult,
    dataId: String,
    context: Context = LocalContext.current
) {
    val poseList = listOf<String>("leftElbowAngle", "rightElbowAngle", "leftHipAngle", "rightHipAngle")
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val canvasWidth: Float
    val canvasHeight: Float
    with(density) {
        canvasWidth = configuration.screenWidthDp.dp.toPx()
        canvasHeight = configuration.screenHeightDp.dp.toPx()
    }

    var isPoseCorrect by remember { mutableStateOf(false) }

    var isSpeaking by remember { mutableStateOf(false) }
    val tts = rememberTextToSpeech()

    if (poseResult.landmarks().isNotEmpty()) {
        val landmarks = poseResult.landmarks()[0]
        val angles = extractPoseAngles2D(landmarks)
        val checks = when(myList[dataId.toInt()].id) {
            1 -> getPushUpAngleChecks(angles)
            2 -> getSquatAngleChecks(angles)
            else -> {
                listOf()
            }
        }

        for(i in 0 .. checks.size step 1) {
            if (tts.value?.isSpeaking == true && checks[i]) {
                tts.value?.stop()
                isSpeaking = false
            } else {
                tts.value?.speak(
                    "wrong pose adjust your ${poseList[i]}", TextToSpeech.QUEUE_FLUSH, null, ""
                )
                isSpeaking = true
            }
        }
        isPoseCorrect = checks.all {it}
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Text(
            text = if (isPoseCorrect) "Correct Pose" else "Incorrect Pose",
            fontSize = 20.sp,
            color = if (isPoseCorrect) Color.Green else Color.Red,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.7f))
                .padding(8.dp)
        )
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
            }
        },
        modifier = modifier
    )
}

@Composable
fun rememberTextToSpeech(): MutableState<TextToSpeech?> {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.US
            }
        }
        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
    return tts
}
