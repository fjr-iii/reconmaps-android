package com.reconmaps.app.runtime.engines

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class PGM1_GPS(
    private val context: Context,
    private val onPosition: (Float, Float) -> Unit
) {

    private lateinit var locationManager: LocationManager
    private var lastGoodLat: Float? = null
    private var lastGoodLon: Float? = null

    @SuppressLint("MissingPermission")
    fun start() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            1f,
            locationListener
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            val lat = location.latitude.toFloat()
            val lon = location.longitude.toFloat()

            // ❌ Reject zero coordinates
            if (lat == 0f && lon == 0f) return

            // ❌ Reject poor accuracy
            if (!location.hasAccuracy()) return
            if (location.accuracy > 50f) return

            // ✅ Accept as GOOD location
            lastGoodLat = lat
            lastGoodLon = lon

            onPosition(lat, lon)
        }
    }
}