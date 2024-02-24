package com.abg.mypaint.local

import android.app.Activity
import android.content.Context
import com.abg.mypaint.brush.BrushType

class Preferences(private val context: Context) {

    companion object {
        private const val MODE = Context.MODE_PRIVATE
        private const val NAME = "paint"
        private const val KEY_STROKE = "stroke"
        private const val KEY_ID = "id"
        private const val LAST_COLOR = "last_color_location"
    }

    fun saveStrokeWidth(lastStrokeWidth: Float) {
        context
            .getSharedPreferences(NAME, MODE)
            .edit()
            .putFloat(KEY_STROKE, lastStrokeWidth)
            .apply()
    }

    fun saveBrushId(id: Int) {
        context
            .getSharedPreferences(NAME, MODE)
            .edit()
            .putInt(KEY_ID, id)
            .apply()
    }

    fun saveLastColorLocation(location: Float) {
        context
            .getSharedPreferences(NAME, Activity.MODE_PRIVATE)
            .edit()
            .putFloat(LAST_COLOR, location)
            .apply();
    }

    fun getStrokeWidth(): Float = context
        .getSharedPreferences(NAME, MODE)
        .getFloat(KEY_STROKE, 12.0f);

    fun getBrushId(): Int = context
        .getSharedPreferences(NAME, MODE)
        .getInt(KEY_ID, BrushType.BRUSH_SOLID)

    fun getLastColorLocation(): Float = context
        .getSharedPreferences(NAME, Activity.MODE_PRIVATE)
        .getFloat(LAST_COLOR, 0.5f)
}