package com.reconmaps.app.runtime.engines

import com.reconmaps.app.runtime.Channel
import com.reconmaps.app.runtime.Vehicle
import com.reconmaps.app.runtime.ConvoyRole

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
        val sortedVehicles = vehicles.values.sortedBy { it.id }

        val firstId = sortedVehicles.firstOrNull()?.id
        val lastId = sortedVehicles.lastOrNull()?.id

        sortedVehicles.forEach { vehicle ->

            val age = now - vehicle.lastUpdate

            val role = vehicle.convoyRole

            val updatedVehicle = if (age > 10_000) {
                vehicle.copy(isStale = true)
            } else {
                vehicle.copy(isStale = false)
            }

            result.add(updatedVehicle)
        }

        return result
    }
}