package com.reconmaps.app.runtime.trail

class TrailManager {

    private val trailPoints = mutableListOf<TrailPoint>()

    private val waypoints = mutableListOf<Waypoint>()
    fun addTrailPoint(
        lat: Double,
        lon: Double,
        timestamp: Long
    ) {
        val lastPoint = trailPoints.lastOrNull()

        if (lastPoint != null) {

            val latDiff = lat - lastPoint.lat
            val lonDiff = lon - lastPoint.lon

            val distanceSquared =
                (latDiff * latDiff) +
                        (lonDiff * lonDiff)

            val minimumDistanceSquared =
                0.000000002

            if (distanceSquared < minimumDistanceSquared) {
                return
            }
        }
        trailPoints.add(
            TrailPoint(
                lat = lat,
                lon = lon,
                timestamp = timestamp
            )
        )
        if (trailPoints.size > 1000) {
            trailPoints.removeAt(0)
        }
    }
    fun addWaypoint(
        id: String,
        lat: Double,
        lon: Double,
        timestamp: Long
    ) {
        waypoints.add(
            Waypoint(
                id = id,
                lat = lat,
                lon = lon,
                timestamp = timestamp
            )
        )
    }

    fun getTrailPoints(): List<TrailPoint> {
        return trailPoints.toList()
    }

    fun getWaypoints(): List<Waypoint> {
        return waypoints.toList()
    }
}