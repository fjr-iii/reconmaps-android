package com.reconmaps.app.runtime.engines

import com.reconmaps.app.runtime.Channel
import com.reconmaps.app.runtime.Vehicle

class PGM4_DataSync(private val selfId: String) {

    private val vehicles = mutableMapOf<String, Vehicle>()

    fun updateVehicle(
        id: String,
        lat: Double,
        lon: Double,
        timestamp: Long,
        isSelf: Boolean
    )
    {
        val existing = vehicles[id]

        // 🔒 Ignore stale packets
        if (existing != null && timestamp < existing.lastUpdate) {
            return
        }

        vehicles[id] = Vehicle(
            id = id,
            lat = lat,
            lon = lon,
            lastUpdate = timestamp,
            isStale = false,
            isSelf = (id == selfId)
        )
        android.util.Log.d("RUNTIME", "SELF FLAG = ${vehicles[id]?.isSelf}")
    }

    fun getVehicles(): List<Vehicle> {
        val now = System.currentTimeMillis()

        val result = mutableListOf<Vehicle>()
        val toRemove = mutableListOf<String>()

        // 🔹 First pass: identify removals
        vehicles.forEach { (id, vehicle) ->
            val age = now - vehicle.lastUpdate

            if (age > 30_000) {
                toRemove.add(id)
            }
        }

        // 🔹 Cleanup BEFORE rendering
        toRemove.forEach { vehicles.remove(it) }

        // 🔹 Second pass: build clean result
        vehicles.forEach { (_, vehicle) ->

            val age = now - vehicle.lastUpdate

            when {
                age > 10_000 -> {
                    result.add(vehicle.copy(isStale = true))
                }
                else -> {
                    result.add(vehicle.copy(isStale = false))
                }
            }
        }

        return result
    }
}