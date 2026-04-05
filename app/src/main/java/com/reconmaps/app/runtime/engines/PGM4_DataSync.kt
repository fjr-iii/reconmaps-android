package com.reconmaps.app.runtime.engines

import com.reconmaps.app.runtime.Channel
import com.reconmaps.app.runtime.Vehicle

class PGM4_Data {

    private val vehicles = mutableMapOf<String, Vehicle>()

    fun updateVehicle(id: String, x: Float, y: Float, channel: Channel) {
        vehicles[id] = Vehicle(
            id = id,
            x = x,
            y = y,
            channel = channel,
            lastUpdate = System.currentTimeMillis(),
            isStale = false
        )
    }

    fun getVehicles(): List<Vehicle> {
        val now = System.currentTimeMillis()

        val result = mutableListOf<Vehicle>()
        val toRemove = mutableListOf<String>()

        vehicles.forEach { (id, vehicle) ->

            val age = now - vehicle.lastUpdate

            when {
                age > 30_000 -> {
                    // ❌ remove
                    toRemove.add(id)
                }

                age > 10_000 -> {
                    // ⚠ stale
                    result.add(vehicle.copy(isStale = true))
                }

                else -> {
                    // ✅ fresh
                    result.add(vehicle.copy(isStale = false))
                }
            }
        }

        // cleanup
        toRemove.forEach { vehicles.remove(it) }

        return result
    }
}