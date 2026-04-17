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

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapCanvasView
    private lateinit var debugView: DebugOverlayView
    private lateinit var buttonBar: ButtonBar
    private lateinit var root: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        org.maplibre.android.MapLibre.getInstance(this)

        mapView = MapCanvasView(this, null)
        mapView.onCreate(savedInstanceState)

        debugView = DebugOverlayView(this)

        root = FrameLayout(this)

        // Add views (ORDER MATTERS)
        root.addView(mapView)
        root.addView(debugView)

        setContentView(root)

        // 🔗 Bind Runtime → Renderer + Debug + ButtonBar
        RuntimeShell.subscribe { state ->
            runOnUiThread {

                mapView.updateVehicles(state.vehicles)
                debugView.update(state)

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
        mapView.getMapView().onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.getMapView().onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.getMapView().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.getMapView().onDestroy()
        super.onDestroy()
    }
}