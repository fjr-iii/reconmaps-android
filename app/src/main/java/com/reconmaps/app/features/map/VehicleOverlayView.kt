package com.reconmaps.app.features.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.reconmaps.app.runtime.render.VehicleRenderData

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
            paint.color = item.color
            canvas.drawCircle(item.x, item.y, 12f, paint)
        }
    }
}