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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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


@Composable
fun PoseCheck(viewPermissionModel: CameraPermissionViewModel = viewModel()){
    val hasPermission by viewPermissionModel.hasPermission.collectAsState(initial = false)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission())
    {
        viewPermissionModel.updatePermissionState()
    }

    LaunchedEffect(Unit){
        if(!hasPermission){
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if(hasPermission){
        val scope = rememberCoroutineScope()
        val currentContext = LocalContext.current
        val controller = remember {
            LifecycleCameraController(currentContext).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE
                )
            }
        }
        val viewModel = viewModel<CameraMediapipeViewModel>()
        val bitmaps by viewModel.bitmaps.collectAsState()
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
            IconButton(onClick = {
                controller.cameraSelector = if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            }) {
                Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "CameraSwitch", modifier = Modifier.padding(8.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ){
                IconButton(onClick = {
                    takePhoto(controller, viewModel::onTakePhoto, currentContext)
                }) {
                    Icon(imageVector = Icons.Default.Camera, contentDescription = "take picture")
                }
            }
        }
    }
}

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
){
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                onPhotoTaken(image.toBitmap())
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("Camera", "Could not take image")
            }
        }
    )
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
){
    val currentContext = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(currentContext)
            }
        },
        modifier = modifier
    )
}