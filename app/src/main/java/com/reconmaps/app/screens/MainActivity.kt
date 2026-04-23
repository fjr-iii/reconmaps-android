package com.reconmaps.app.screens

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.reconmaps.app.features.map.MapCanvasView
import com.reconmaps.app.runtime.RuntimeShell
import com.reconmaps.app.devtools.DebugOverlayView
import com.reconmaps.app.features.controls.ButtonBar
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.reconmaps.app.features.map.VehicleOverlayView




class MainActivity : AppCompatActivity() {
    lateinit var mapCanvasView: MapCanvasView
    private var hasCenteredCamera = false
    lateinit var vehicleOverlayView: VehicleOverlayView

    private lateinit var debugView: DebugOverlayView
    private lateinit var buttonBar: ButtonBar
    private lateinit var root: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        org.maplibre.android.MapLibre.getInstance(this)

        // Create map view
        mapCanvasView = MapCanvasView(this, null)
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

                val map = mapCanvasView.getMapLibreMap()

                if (map != null) {

                    val renderData = state.vehicles.map { vehicle ->

                        val point = map.projection.toScreenLocation(
                            org.maplibre.android.geometry.LatLng(
                                vehicle.lat.toDouble(),
                                vehicle.lon.toDouble()
                            )
                        )

                        com.reconmaps.app.runtime.render.VehicleRenderData(
                            x = point.x.toFloat(),
                            y = point.y.toFloat(),
                            color = android.graphics.Color.BLUE
                        )
                    }
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