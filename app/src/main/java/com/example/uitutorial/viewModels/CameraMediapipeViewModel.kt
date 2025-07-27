package com.example.uitutorial.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CameraMediapipeViewModel : ViewModel(){
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps

    fun onTakePhoto(bitmap: Bitmap){
        _bitmaps.value += bitmap
    }
}