package com.abg.mypaint

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

internal fun Context.showGrantedToast() {
    val msg = getString(R.string.granted_permissions)
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

internal fun Context.showRationaleDialog(request: PermissionRequest) {
    val msg = getString(R.string.rationale_permissions)

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.request_again) { _, _ ->
            // Send the request again.
            request.send()
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

internal fun Context.showPermanentlyDeniedDialog() {
    val msg = getString(R.string.permanently_denied_permissions)

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.action_settings) { _, _ ->
            // Open the app's settings.
            val intent = createAppSettingsIntent()
            startActivity(intent)
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

private fun Context.createAppSettingsIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}