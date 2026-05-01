package com.reconmaps.app.features.map

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout

import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

class MapCanvasView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    var onUserInteraction: (() -> Unit)? = null

    private val mapView = MapView(context)
    // 🔑 This is what we expose for projection
    private var mapLibreMap: MapLibreMap? = null

    init {
        addView(
            mapView,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        )
    }

    fun onCreate(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->

            map.addOnMoveListener(object : org.maplibre.android.maps.MapLibreMap.OnMoveListener {

                override fun onMoveBegin(detector: org.maplibre.android.gestures.MoveGestureDetector) {
                    onUserInteraction?.invoke()
                }

                override fun onMove(detector: org.maplibre.android.gestures.MoveGestureDetector) {}

                override fun onMoveEnd(detector: org.maplibre.android.gestures.MoveGestureDetector) {}
            })

            mapLibreMap = map

            map.setStyle(org.maplibre.android.maps.Style.Builder()) { style ->

                val rasterSource = org.maplibre.android.style.sources.RasterSource(
                    "osm-source",
                    org.maplibre.android.style.sources.TileSet(
                        "2.0",
                        "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                    ),
                    256
                )

                style.addSource(rasterSource)

                val rasterLayer = org.maplibre.android.style.layers.RasterLayer(
                    "osm-layer",
                    "osm-source"
                )

                style.addLayer(rasterLayer)

                android.util.Log.d("MAP", "OSM raster tiles loaded")
            }
        }
    }

    // --------------------------------------------------
    // ACCESSORS
    // --------------------------------------------------
    fun getMapView(): MapView {
        return mapView
    }
    fun getMapLibreMap(): MapLibreMap? {
        return mapLibreMap
    }
    // --------------------------------------------------
    // CAMERA CONTROL
    // --------------------------------------------------
    fun moveCamera(lat: Double, lon: Double) {
        mapLibreMap?.let { map ->

            val position = CameraPosition.Builder()
                .target(LatLng(lat, lon))
                .zoom(18.0)
                .build()

            map.cameraPosition = position
        }
    }

    // --------------------------------------------------
    // LIFECYCLE
    // --------------------------------------------------

    fun onStart() {
        mapView.onStart()
    }

    fun onResume() {
        mapView.onResume()
    }

    fun onPause() {
        mapView.onPause()
    }

    fun onStop() {
        mapView.onStop()
    }

    fun onDestroy() {
        mapView.onDestroy()
    }
}