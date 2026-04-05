package com.reconmaps.app.features.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.reconmaps.app.runtime.Vehicle

class MapCanvasView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
    }

    private var vehicles: List<Vehicle> = emptyList()

    fun updateVehicles(vehicles: List<Vehicle>) {
        this.vehicles = vehicles
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Background
        canvas.drawColor(Color.BLACK)

        // Draw all vehicles
        vehicles.forEach { vehicle ->

            if (vehicle.isStale) {
                paint.color = Color.GRAY
            } else {
                paint.color = Color.BLUE
            }

            canvas.drawCircle(vehicle.x, vehicle.y, 20f, paint)
        }
    }
}