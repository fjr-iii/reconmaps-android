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
            0L,
            0f,
            locationListener
        )

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            android.util.Log.d("GPS_DEBUG", "Provider=${location.provider} Acc=${location.accuracy}")

            val lat = location.latitude.toFloat()
            val lon = location.longitude.toFloat()

            // ❌ Reject zero coordinates
            if (lat == 0f && lon == 0f) return

            if (!location.hasAccuracy()) return

            // Allow network initially, filter only very bad data
            if (location.accuracy > 1000f) return

            // ✅ Accept as GOOD location
            lastGoodLat = lat
            lastGoodLon = lon

            onPosition(lat, lon)
        }
    }
}