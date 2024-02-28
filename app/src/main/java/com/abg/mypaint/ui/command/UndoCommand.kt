package com.abg.mypaint.ui.command

import android.view.View
import com.abg.mypaint.ui.FingerPaintView

class UndoCommand(private val fingerPaintView: FingerPaintView) : View.OnClickListener {
    override fun onClick(v: View) {
        fingerPaintView.onUndo()
    }
}