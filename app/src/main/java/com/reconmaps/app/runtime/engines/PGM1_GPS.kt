package com.reconmaps.app.runtime.engines

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class PGM1_GPS(
    private val context: Context,
    )
{
    var lastLat: Double? = null
    var lastLon: Double? = null
    private lateinit var locationManager: LocationManager
    var lastGoodLat: Double? = null
    var lastGoodLon: Double? = null

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

            val lat = location.latitude
            val lon = location.longitude


// ❌ Reject zero coordinates
            if (lat == 0.0 && lon == 0.0) return

            if (!location.hasAccuracy()) return

// Allow network initially, filter only very bad data
            if (location.accuracy > 35f) return

// ✅ Accept as GOOD location
            lastGoodLat = lat
            lastGoodLon = lon


        }
    }
}