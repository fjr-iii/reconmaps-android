package com.reconmaps.app.runtime.render

import android.graphics.Color
import com.reconmaps.app.runtime.Vehicle

class RenderTransformer {

    fun transform(vehicles: List<Vehicle>): List<VehicleRenderData> {

        return vehicles.map { vehicle ->

            // TEMP: simple projection (replace later with real map projection)
            val x = vehicle.lon * 10f
            val y = vehicle.lat * 10f

            val color = if (vehicle.isStale) {
                Color.GRAY
            } else {
                Color.BLUE
            }

            VehicleRenderData(
                x = x,
                y = y,
                color = color
            )
        }
    }
}