package com.reconmaps.app.runtime.engines

data class TransportPacket(
    val deviceId: String,
    val timestamp: Long,
    val lat: Double,
    val lon: Double
)