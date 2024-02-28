package com.abg.mypaint.local

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import com.abg.mypaint.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ManagerFile {
    
    fun saveImage(bitmap: Bitmap, context: Context) {
        val sdf = SimpleDateFormat("yyyyMMdd_HH_mm_SS", Locale.US)
        val format = sdf.format(Date())
        MediaStore.Images.Media.insertImage(
            context.contentResolver, bitmap, format, ""
        )

        Toast.makeText(context, context.getString(R.string.save_to), Toast.LENGTH_SHORT).show()

    }

}