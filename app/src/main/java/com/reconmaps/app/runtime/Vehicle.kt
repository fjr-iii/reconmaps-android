package com.reconmaps.app.runtime

data class Vehicle(
    val id: String,
    val x: Float,
    val y: Float,
    val channel: Channel,
    val lastUpdate: Long,
    val isStale: Boolean = false
)