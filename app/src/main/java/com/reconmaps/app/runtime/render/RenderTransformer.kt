package com.reconmaps.app.runtime.render

import android.graphics.Color
import com.reconmaps.app.runtime.Vehicle

class RenderTransformer {

    fun transform(vehicles: List<Vehicle>): List<VehicleRenderData> {

        return listOf(
            VehicleRenderData(
                x = 500f,
                y = 500f,
                color = android.graphics.Color.RED
            )
        )
    }
}