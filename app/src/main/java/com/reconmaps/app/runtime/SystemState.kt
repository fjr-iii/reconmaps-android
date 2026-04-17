package com.reconmaps.app.runtime

data class SystemState(
    val vehicles: List<Vehicle>,
    val gpsEnabled: Boolean,
    val transportOnline: Boolean,
    val channel: Channel,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)