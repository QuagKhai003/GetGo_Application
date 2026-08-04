package com.quangkhai.getgo_application.presentation.ui.shared

import android.content.Context
import android.graphics.drawable.GradientDrawable

// a filled circle with a black border, used for friend / my-location map markers
fun quaternaryCircleDotDrawable(context: Context, color: Int): GradientDrawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
        setStroke((2 * context.resources.displayMetrics.density).toInt(), android.graphics.Color.BLACK)
        val size = (26 * context.resources.displayMetrics.density).toInt()
        setSize(size, size)
    }
}
