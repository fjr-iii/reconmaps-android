package com.reconmaps.app.runtime

data class Vehicle(
    val id: String,
    val lat: Double,
    val lon: Double,
    val lastUpdate: Long,
    val isStale: Boolean,
    val isSelf: Boolean
)