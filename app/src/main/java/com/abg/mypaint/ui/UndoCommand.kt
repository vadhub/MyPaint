package com.abg.mypaint.ui

import android.view.View

class UndoCommand(private val fingerPaintView: FingerPaintView) : View.OnClickListener {
    override fun onClick(v: View) {
        fingerPaintView.onUndo()
    }
}