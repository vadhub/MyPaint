package com.abg.mypaint.ui.ads

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class DefaultProcessLifecycleObserver(
    private val onProcessCameForeground: () -> Unit
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        onProcessCameForeground()
    }
}