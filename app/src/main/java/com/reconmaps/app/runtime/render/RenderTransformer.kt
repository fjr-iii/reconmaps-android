package com.reconmaps.app.runtime.render

import com.reconmaps.app.runtime.Vehicle
import com.reconmaps.app.runtime.ConvoyRole

class RenderTransformer {

    fun transform(
        vehicles: List<Vehicle>,
        map: org.maplibre.android.maps.MapLibreMap,
        zoom: Double
    ): List<VehicleRenderData> {

        return vehicles.map { vehicle ->

            android.util.Log.d(
                "RENDER",
                "vehicle=${vehicle.id} stale=${vehicle.isStale} zoom=$zoom"
            )

            val point = map.projection.toScreenLocation(
                org.maplibre.android.geometry.LatLng(
                    vehicle.lat,
                    vehicle.lon
                )
            )

            VehicleRenderData(
                x = point.x,
                y = point.y,
                color = android.graphics.Color.BLUE,
                role = when {
                    vehicle.isSelf -> MarkerRole.SELF
                    vehicle.convoyRole == ConvoyRole.LEADER -> MarkerRole.LEADER
                    vehicle.convoyRole == ConvoyRole.SWEEP -> MarkerRole.SWEEP
                    else -> MarkerRole.VEHICLE
                },
                label = when {
                    vehicle.isSelf -> "YOU"
                    vehicle.convoyRole == ConvoyRole.LEADER -> "LEAD"
                    vehicle.convoyRole == ConvoyRole.SWEEP -> "SWEEP"
                    else -> vehicle.id.take(4)
                },

                isStale = vehicle.isStale,
                showLabel = !vehicle.isStale && zoom >= 17.0,
                markerScale = when {
                    zoom >= 17.0 -> 1.0f
                    zoom >= 15.0 -> 0.8f
                    else -> 0.6f
                }
            )
        }
    }
}