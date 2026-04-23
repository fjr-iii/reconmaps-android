package com.reconmaps.app.devtools

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import java.util.Locale
import com.reconmaps.app.runtime.SystemState


class DebugOverlayView(context: Context) : AppCompatTextView(context) {
    private val startTime = System.currentTimeMillis()

    private fun getElapsedTime(): String {
        val elapsed = System.currentTimeMillis() - startTime

        val seconds = (elapsed / 1000) % 60
        val minutes = (elapsed / (1000 * 60)) % 60
        val hours = (elapsed / (1000 * 60 * 60))

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    init {
        setTextColor(Color.GREEN)
        setBackgroundColor(Color.argb(120, 0, 0, 0))
        setPadding(20, 20, 20, 20)
        textSize = 14f
        gravity = Gravity.START

        translationX = 20f
        translationY = 40f
    }

    @Suppress("SetTextI18n")
    fun update(state: SystemState) {

        val self = state.vehicles.firstOrNull()

        if (self != null) {

            val latStr = String.format(Locale.US, "%.5f", self.lat)
            val lonStr = String.format(Locale.US, "%.5f", self.lon)

            text = buildString {
                append("TIME: ${getElapsedTime()}\n")
                append("GPS: $latStr, $lonStr\n")
                append("GPS Status: ${if (state.gpsEnabled) "ON" else "OFF"}\n")
                append("Transport: ${if (state.transportOnline) "ONLINE" else "OFFLINE"}\n")
                append("Channel: ${state.channel.name}")
            }

        } else {
            text = "Waiting for GPS..."
        }
    }
}