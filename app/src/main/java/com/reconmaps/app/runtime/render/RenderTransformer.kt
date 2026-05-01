package com.reconmaps.app.runtime.render

import com.reconmaps.app.runtime.Vehicle

class RenderTransformer {

    fun transform(
        vehicles: List<Vehicle>,
        map: org.maplibre.android.maps.MapLibreMap
    ): List<VehicleRenderData> {

        return vehicles.map { vehicle ->

            val point = map.projection.toScreenLocation(
                org.maplibre.android.geometry.LatLng(
                    vehicle.lat,
                    vehicle.lon
                )
            )

            VehicleRenderData(
                x = point.x.toFloat(),
                y = point.y.toFloat(),
                color = if (vehicle.isSelf) {
                    android.graphics.Color.BLUE
                } else {
                    android.graphics.Color.RED
                }
            )
        }
    }
}