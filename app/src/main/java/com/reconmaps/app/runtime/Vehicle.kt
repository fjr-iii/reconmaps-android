package com.reconmaps.app.runtime

data class Vehicle(
    val id: String,
    val lat: Float,
    val lon: Float,
    val channel: Channel,
    val lastUpdate: Long,
    val isStale: Boolean = false
)