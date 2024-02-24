package com.abg.mypaint.ui.brush.brushtypes

import android.view.View
import com.abg.mypaint.ui.FingerPaintView
import com.abg.mypaint.ui.brush.BrushOption
import com.abg.mypaint.ui.brush.BrushType

class DebossBrushCommand(
    private val brushOption: BrushOption,
    private val fingerPaintView: FingerPaintView
) : View.OnClickListener {

    override fun onClick(v: View?) {
        fingerPaintView.setBrushType(BrushType.BRUSH_DEBOSS)
        brushOption.showBrushOptions()
    }
}