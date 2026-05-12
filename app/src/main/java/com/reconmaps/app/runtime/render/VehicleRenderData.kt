package com.reconmaps.app.runtime.render

data class VehicleRenderData(
    val x: Float,
    val y: Float,
    val color: Int,
    val role: MarkerRole,
    val label: String,
    val isStale: Boolean,
    val showLabel: Boolean,
    val markerScale: Float
)