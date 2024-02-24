package com.abg.mypaint.ui.brush

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.abg.mypaint.R

class BrushOption(private val selectBrushFrame: LinearLayout, private val strokeWidthFrame: LinearLayout) {

    fun showBrushOptions() {
        val isVisible = selectBrushFrame.visibility != View.VISIBLE
        val context = selectBrushFrame.context
        if (isVisible) {
            selectBrushFrame.visibility = View.VISIBLE
            selectBrushFrame.animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        } else {
            selectBrushFrame.visibility = View.GONE
            selectBrushFrame.animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)
        }
        strokeWidthFrame.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}