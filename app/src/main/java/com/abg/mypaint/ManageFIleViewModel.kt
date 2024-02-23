package com.abg.mypaint

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class ManageFIleViewModel : ViewModel() {
    fun saveFile(bitmap: Bitmap) {
        ManagerFile.storePhotoOnDisk(bitmap)
    }
}