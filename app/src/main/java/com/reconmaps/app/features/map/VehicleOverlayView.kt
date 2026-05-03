package com.reconmaps.app.features.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.reconmaps.app.runtime.render.VehicleRenderData
import com.reconmaps.app.runtime.render.MarkerConfig


class VehicleOverlayView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var renderData: List<VehicleRenderData> = emptyList()

    fun setRenderData(renderData: List<VehicleRenderData>) {
        this.renderData = renderData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (item in renderData) {

            val style = MarkerConfig.styles[item.role]
            val color = style?.color ?: android.graphics.Color.WHITE

            if (style?.hasHalo == true) {

                // Outer ring
                paint.color = color
                canvas.drawCircle(item.x, item.y, 20f, paint)

                // White halo
                paint.color = android.graphics.Color.WHITE
                canvas.drawCircle(item.x, item.y, 14f, paint)

                // Core
                paint.color = color
                canvas.drawCircle(item.x, item.y, 8f, paint)

            } else {

                paint.color = color
                canvas.drawCircle(item.x, item.y, 12f, paint)
            }
        }
    }
}