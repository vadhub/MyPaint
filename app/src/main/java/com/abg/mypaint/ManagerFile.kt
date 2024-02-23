package com.abg.mypaint

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ManagerFile {
    fun storePhotoOnDisk(capturedBitmap: Bitmap) {
        val pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDirectory = File(pictureDirectory, "MyPaint")
        if (!appDirectory.exists()) {
            appDirectory.mkdir()
        val sdf = SimpleDateFormat("yyyyMMdd_HH_mm_SS", Locale.US)
        val format = sdf.format(Date())
        val photoFile = File(appDirectory, "$format.jpg")

        try {
            val fos = FileOutputStream(photoFile.path)
            capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            Log.e("PictureDemo", "Exception in photoCallback", e)
        }
        }
    }
}