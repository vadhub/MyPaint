package com.abg.mypaint

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.abg.mypaint.local.ManagerFile
import com.abg.mypaint.ui.DrawableFragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.isPermissionGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest

class MainActivity : AppCompatActivity(), DrawableFragment.FileHandler, PermissionRequest.Listener {

    companion object {
        private const val REQUEST_CODE_OPEN_DIRECTORY = 13433
    }

    private val request by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsBuilder(Manifest.permission.READ_MEDIA_IMAGES).build()
        } else {
            permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).build()
        }
    }

    private val isPermissionGranted by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request.addListener(this)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container,
            DrawableFragment()
        ).commit()
    }

    override fun onSave(bitmap: Bitmap) {

        if (isPermissionGranted) {
            ManagerFile.saveImage(bitmap, this)
        } else {
            request.send()

        }
    }

    override fun openImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("application/pdf")
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> showRationaleDialog(result, request)
            result.allGranted() -> {
                showGrantedToast(result)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == AppCompatActivity.RESULT_OK) {
            Log.d("ddd", String.format("Open Directory result Uri : %s", data!!.data))
            val args = Bundle()
            args.putString("uri", data.data.toString())

        }
    }
}