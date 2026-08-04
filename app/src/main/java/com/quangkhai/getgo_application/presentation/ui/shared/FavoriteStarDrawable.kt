package com.quangkhai.getgo_application.presentation.ui.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable

// a star glyph (colour fill + black outline) drawn to a bitmap, used for favorite map markers
fun favoriteStarDrawable(context: Context, color: Int): BitmapDrawable {
    val size = (29 * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = size * 0.85f
        textAlign = Paint.Align.CENTER
    }
    val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = size * 0.05f
        textSize = size * 0.85f
        textAlign = Paint.Align.CENTER
    }

    val canvas = Canvas(bitmap)
    val y = size / 2f - (fill.descent() + fill.ascent()) / 2f
    canvas.drawText("★", size / 2f, y, fill)
    canvas.drawText("★", size / 2f, y, outline)

    return BitmapDrawable(context.resources, bitmap)
}
