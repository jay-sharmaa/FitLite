package com.example.fitlite.viewModels

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CameraMediapipeViewModel : ViewModel(){
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    var cameraFacing by mutableIntStateOf(CameraSelector.LENS_FACING_BACK)

    fun onTakePhoto(bitmap: Bitmap){
        _bitmaps.value += bitmap
    }
    fun toggleCameraFacing() {
        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }
}