package com.abg.mypaint

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abg.mypaint.painview.DrawableOnTouchView
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.isPermissionGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest

class MainActivity : AppCompatActivity(), DrawableOnTouchView.FileHandler, PermissionRequest.Listener {

    private val request by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsBuilder(Manifest.permission.READ_MEDIA_IMAGES).build()
        } else {
            permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        }
    }

    private val isPermissionGranted by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request.addListener(this)
        val mainFrame: FrameLayout = findViewById(R.id.main_frame)
        try {
            val drawableOnTouchView = DrawableOnTouchView(this)
            drawableOnTouchView.setFileHandler(this)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            mainFrame.addView(drawableOnTouchView, params)
            drawableOnTouchView.attachCanvas(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        } catch (e: Exception) {
            Log.e("MainActivity", e.message!!)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSave(bitmap: Bitmap) {

        if (isPermissionGranted) {
            ManagerFile.storePhotoOnDisk(bitmap)
        } else {
            request.send()

        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> showRationaleDialog(result, request)
            result.allGranted() -> showGrantedToast(result)
        }
    }
}