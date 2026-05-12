package com.reconmaps.app.screens

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.reconmaps.app.runtime.SystemState
import com.reconmaps.app.features.map.MapCanvasView
import com.reconmaps.app.runtime.RuntimeShell
import com.reconmaps.app.devtools.DebugOverlayView
import com.reconmaps.app.features.controls.ButtonBar
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.reconmaps.app.features.map.VehicleOverlayView
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var mapCanvasView: MapCanvasView
    private var hasCenteredCamera = false
    lateinit var vehicleOverlayView: VehicleOverlayView

    private lateinit var debugView: DebugOverlayView
    private lateinit var buttonBar: ButtonBar
    private lateinit var root: FrameLayout
    private val renderTransformer = com.reconmaps.app.runtime.render.RenderTransformer()

    private var lastCameraLat: Double? = null
    private var lastCameraLon: Double? = null
    private var followMode: Boolean = true
    private var currentState: SystemState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        org.maplibre.android.MapLibre.getInstance(this)

        // Create map view
        mapCanvasView = MapCanvasView(this, null)

        mapCanvasView.onUserInteraction = {
            followMode = false
        }
        mapCanvasView.onCreate(savedInstanceState)

        // Debug overlay
        debugView = DebugOverlayView(this)

// Root layout
        root = FrameLayout(this)

// Add views (ORDER MATTERS)
        root.addView(mapCanvasView)
        vehicleOverlayView = VehicleOverlayView(this)
        root.addView(vehicleOverlayView)

        val debugParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        debugParams.gravity = android.view.Gravity.TOP or android.view.Gravity.START

        debugView.layoutParams = debugParams

        root.addView(debugView)

        setContentView(root)

        // 🔗 Bind Runtime → UI (THIS is the correct connection)
        RuntimeShell.subscribe { state ->
            runOnUiThread {

                currentState = state

                val map = mapCanvasView.getMapLibreMap()

                if (map != null) {

                    android.util.Log.d("DEBUG", "VEHICLE COUNT = ${state.vehicles.size}")

                    val renderData = renderTransformer.transform(
                        state.vehicles,
                        map,
                        map.cameraPosition.zoom.toDouble()
                    )
                    val selfVehicle = state.vehicles.find { it.isSelf }

                    if (selfVehicle != null && followMode) {

                        val newLat = selfVehicle.lat
                        val newLon = selfVehicle.lon

                        if (lastCameraLat == null || lastCameraLon == null) {

                            // First time — always move camera
                            map.moveCamera(
                                org.maplibre.android.camera.CameraUpdateFactory.newLatLng(
                                    org.maplibre.android.geometry.LatLng(newLat, newLon)
                                )
                            )

                            lastCameraLat = newLat
                            lastCameraLon = newLon

                        } else {

                            val latDiff = Math.abs(newLat - lastCameraLat!!)
                            val lonDiff = Math.abs(newLon - lastCameraLon!!)

                            val threshold = 0.00001  // ~5–6 meters (1m for testing)

                            if (latDiff > threshold || lonDiff > threshold) {

                                map.moveCamera(
                                    org.maplibre.android.camera.CameraUpdateFactory.newLatLng(
                                        org.maplibre.android.geometry.LatLng(newLat, newLon)
                                    )
                                )

                                lastCameraLat = newLat
                                lastCameraLon = newLon
                            }
                        }
                    }

                    vehicleOverlayView.setProjection { lat, lon ->

                        mapCanvasView.project(lat, lon)
                            ?: Pair(0f, 0f)
                    }
                    android.util.Log.d("UI", "RENDER DATA SIZE = ${renderData.size}")
                    vehicleOverlayView.setRenderData(renderData)
                }

                debugView.update(state)

                val self = state.vehicles.firstOrNull()

                if (self != null && !hasCenteredCamera) {

                    android.util.Log.d("CAMERA", "LOCKING CAMERA ONCE")

                    mapCanvasView.moveCamera(

                        self.lat.toDouble(),
                        self.lon.toDouble()
                    )

                    hasCenteredCamera = true
                }
                if (::buttonBar.isInitialized) {
                    root.removeView(buttonBar)
                }

                buttonBar = ButtonBar(
                    context = this,
                    systemState = state,
                    onGpsToggle = { RuntimeShell.toggleGps() },
                    onTrackingToggle = { RuntimeShell.toggleTracking() },
                    onChannelNext = { RuntimeShell.nextChannel() }
                )

                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )

                params.gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                buttonBar.layoutParams = params

                root.addView(buttonBar)

                val recenterBtn = Button(this).apply {
                    text = "CENTER"
                }

                val recenterParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
                    bottomMargin = 350   // ← THIS now works correctly
                    marginEnd = 40
                }

                root.addView(recenterBtn, recenterParams)

                recenterBtn.setOnClickListener {
                    buttonBar.onRecenterClicked?.invoke()
                }

                buttonBar.onRecenterClicked = {
                    followMode = true

                    val map = mapCanvasView.getMapLibreMap()
                    val selfVehicle = currentState?.vehicles?.find { it.isSelf }

                    if (map != null && selfVehicle != null) {
                        map.moveCamera(
                            org.maplibre.android.camera.CameraUpdateFactory.newLatLng(
                                org.maplibre.android.geometry.LatLng(
                                    selfVehicle.lat,
                                    selfVehicle.lon
                                )
                            )
                        )

                        lastCameraLat = selfVehicle.lat
                        lastCameraLon = selfVehicle.lon
                    }
                }
            }
        }


        // ✅ Permission check BEFORE starting Runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )

        } else {
            RuntimeShell.start(this)
        }
    }

    // ✅ MUST be outside onCreate
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            RuntimeShell.start(this)
        }
    }
    override fun onStart() {
        super.onStart()
        mapCanvasView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapCanvasView.onResume()
    }

    override fun onPause() {
        mapCanvasView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapCanvasView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapCanvasView.onDestroy()
        super.onDestroy()
    }

}