package com.reconmaps.app.features.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.reconmaps.app.runtime.trail.TrailPoint
import android.util.AttributeSet
import android.view.View
import com.reconmaps.app.runtime.render.VehicleRenderData
import com.reconmaps.app.runtime.render.MarkerConfig
import com.reconmaps.app.runtime.render.MarkerRole


class VehicleOverlayView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val trailPaint = Paint().apply {
        color = android.graphics.Color.CYAN
        strokeWidth = 6f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val trianglePath = android.graphics.Path()

    private var renderData: List<VehicleRenderData> = emptyList()
    private var trailPoints: List<TrailPoint> = emptyList()
    private var projectPoint: ((Double, Double) -> Pair<Float, Float>)? = null

    fun setRenderData(renderData: List<VehicleRenderData>) {
        this.renderData = renderData
        this.trailPoints = com.reconmaps.app.runtime.RuntimeShell.getTrailPoints()
        invalidate()
    }

    fun setProjection(
        projection: (Double, Double) -> Pair<Float, Float>
    ) {
        projectPoint = projection
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (trailPoints.size > 1) {

            val path = Path()

            trailPoints.forEachIndexed { index, point ->

                val projection = projectPoint ?: return@forEachIndexed

                val projected = projection(point.lat, point.lon)

                val x = projected.first
                val y = projected.second

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            canvas.drawPath(path, trailPaint)
        }

        val sortedData = renderData.sortedByDescending {
            MarkerConfig.styles[it.role]?.priority ?: 0
        }

        for (item in sortedData) {

            android.util.Log.d("MARKER_DEBUG", "ROLE = ${item.role}")

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

                val radius = when (item.role) {
                    MarkerRole.LEADER -> 30f * item.markerScale
                    MarkerRole.SWEEP -> 30f * item.markerScale
                    else -> 12f * item.markerScale
                }

                paint.color = if (
                    item.isStale &&
                    item.role != MarkerRole.SELF &&
                    item.role != MarkerRole.LEADER &&
                    item.role != MarkerRole.SWEEP
                ) {
                    android.graphics.Color.BLACK
                } else {
                    color
                }

                when (style?.shape) {

                    MarkerConfig.Shape.CIRCLE -> {
                        canvas.drawCircle(item.x, item.y, radius, paint)
                    }

                    MarkerConfig.Shape.SQUARE -> {
                        canvas.drawRect(
                            item.x - radius,
                            item.y - radius,
                            item.x + radius,
                            item.y + radius,
                            paint
                        )
                    }

                    MarkerConfig.Shape.TRIANGLE -> {
                        trianglePath.reset()
                        trianglePath.moveTo(item.x, item.y - radius)
                        trianglePath.lineTo(item.x - radius, item.y + radius)
                        trianglePath.lineTo(item.x + radius, item.y + radius)
                        trianglePath.close()
                        canvas.drawPath(trianglePath, paint)
                    }

                    MarkerConfig.Shape.DIAMOND -> {
                        trianglePath.reset()
                        trianglePath.moveTo(item.x, item.y - radius)
                        trianglePath.lineTo(item.x - radius, item.y)
                        trianglePath.lineTo(item.x, item.y + radius)
                        trianglePath.lineTo(item.x + radius, item.y)
                        trianglePath.close()
                        canvas.drawPath(trianglePath, paint)
                    }

                    MarkerConfig.Shape.CROSS -> {
                        val thickness = radius / 3

                        // vertical
                        canvas.drawRect(
                            item.x - thickness,
                            item.y - radius,
                            item.x + thickness,
                            item.y + radius,
                            paint
                        )

                        // horizontal
                        canvas.drawRect(
                            item.x - radius,
                            item.y - thickness,
                            item.x + radius,
                            item.y + thickness,
                            paint
                        )
                    }

                    MarkerConfig.Shape.DOT -> {
                        canvas.drawCircle(item.x, item.y, radius / 3, paint)
                    }

                    else -> {
                        canvas.drawCircle(item.x, item.y, radius, paint)
                    }
                }
            }
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 28f
            paint.textAlign = Paint.Align.CENTER

            if (item.showLabel) {

                canvas.drawText(
                    item.label,
                    item.x,
                    item.y - 40f,
                    paint
                )

            }
        }
    }
}