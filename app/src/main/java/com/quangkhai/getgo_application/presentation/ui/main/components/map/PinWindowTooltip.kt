package com.quangkhai.getgo_application.presentation.ui.main.components.map

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
    The window tooltip that appears when you tap the pin - its also has red cross to remove the pin
*/
class PinWindowTooltip(
    mapView: MapView,
    private val onDelete: () -> Unit
) : InfoWindow(buildBubble(mapView.context), mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as? Marker
        mView.findViewWithTag<TextView>(TAG_TITLE)?.text = marker?.title.orEmpty()
        mView.findViewWithTag<TextView>(TAG_CLOSE)?.setOnClickListener {
            close()       // hide the bubble
            onDelete()    // remove the pin
        }
        // tapping the bubble itself just closes it
        mView.setOnClickListener { close() }
    }

    override fun onClose() {}

    companion object {
        private const val TAG_TITLE = "info_title"
        private const val TAG_CLOSE = "info_close"

        private fun dp(context: Context, value: Float): Int =
            (value * context.resources.displayMetrics.density).toInt()

        private fun buildBubble(context: Context): View {
            val root = FrameLayout(context)

            // white rounded card, no border, compact padding (no gap below the text)
            val card = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                background = PaintDrawable(Color.WHITE).apply {
                    setCornerRadius(dp(context, 12f).toFloat())
                }
                setPadding(dp(context, 14f), dp(context, 9f), dp(context, 14f), dp(context, 9f))
            }
            val title = TextView(context).apply {
                tag = TAG_TITLE
                setTextColor(Color.parseColor("#14202F"))
                textSize = 14f
                setTypeface(typeface, Typeface.BOLD)
                maxWidth = dp(context, 220f)
            }
            card.addView(title)

            root.addView(
                card,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(context, 11f)
                    rightMargin = dp(context, 11f)
                }
            )

            // red close circle at the top-right
            val close = TextView(context).apply {
                tag = TAG_CLOSE
                text = "✕"
                setTextColor(Color.WHITE)
                textSize = 11f
                gravity = Gravity.CENTER
                background = ShapeDrawable(OvalShape()).apply {
                    paint.color = Color.parseColor("#E11D26")
                }
            }
            root.addView(
                close,
                FrameLayout.LayoutParams(
                    dp(context, 22f), dp(context, 22f), Gravity.TOP or Gravity.END
                )
            )

            return root
        }
    }
}
