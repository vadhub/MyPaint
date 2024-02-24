package com.abg.mypaint.ui

import android.view.View
import com.abg.mypaint.ui.DrawableFragment.FileHandler

class SaveCommand(
    private val fileHandler: FileHandler,
    private val fingerPaintView: FingerPaintView
) : View.OnClickListener {
    override fun onClick(v: View?) {
        fileHandler.onSave(fingerPaintView.getmBitmap())
    }

}