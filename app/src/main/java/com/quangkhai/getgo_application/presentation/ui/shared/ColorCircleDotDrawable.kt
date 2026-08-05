package com.quangkhai.getgo_application.presentation.ui.shared

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

// a filled circle dot for map markers. Pass fill + border colour and size so the
// same shape works for friends, my location, discovered places, ...
fun colorCircleDotDrawable(
    context: Context,
    fillColor: Int,
    borderColor: Int = Color.BLACK,
    sizeDp: Int = 26,
    borderWidthDp: Int = 2
): GradientDrawable {
    val density = context.resources.displayMetrics.density
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(fillColor)
        setStroke((borderWidthDp * density).toInt(), borderColor)
        val size = (sizeDp * density).toInt()
        setSize(size, size)
    }
}
