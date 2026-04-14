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
        android.util.Log.d("MAP", "Drawing vehicles: ${vehicles.size}")
        super.onDraw(canvas)

        // Background
        canvas.drawColor(Color.BLACK)

        val self = vehicles.find { it.id == "SELF" }

        if (self == null) return

        val now = System.currentTimeMillis()

        vehicles.forEach { vehicle ->

            if (now - vehicle.lastUpdate > 5000) return@forEach

            when {
                vehicle.id == "SELF" -> paint.color = Color.RED
                vehicle.isStale -> paint.color = Color.GRAY
                else -> paint.color = Color.BLUE
            }

            val centerX = width / 2f
            val centerY = height / 2f
            val scale = 1000000f

            val dx = vehicle.y - self.y
            val dy = vehicle.x - self.x

            val screenX = centerX + (dx * scale)
            val screenY = centerY - (dy * scale)

            canvas.drawCircle(screenX, screenY, 20f, paint)
        }
    }
}