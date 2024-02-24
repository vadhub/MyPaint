package com.abg.mypaint.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ManagerFile {
    
    fun saveImage(bitmap: Bitmap, context: Context) {
        val sdf = SimpleDateFormat("yyyyMMdd_HH_mm_SS", Locale.US)
        val format = sdf.format(Date())
        val uri = MediaStore.Images.Media.insertImage(
            context.contentResolver, bitmap, format, ""
        )

        Toast.makeText(context, Uri.decode(uri), Toast.LENGTH_SHORT).show()

    }

}