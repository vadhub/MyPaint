package com.abg.mypaint.ui

import android.view.View
import android.widget.LinearLayout

class StrokeWidthCommand(private val strokeWidthFrame: LinearLayout) : View.OnClickListener {
    override fun onClick(v: View?) {
        strokeWidthFrame.visibility = if (strokeWidthFrame.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }
}