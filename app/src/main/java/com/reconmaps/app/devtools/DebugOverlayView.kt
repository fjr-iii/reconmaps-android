package com.reconmaps.app.devtools

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.reconmaps.app.R

class DebugOverlayView(context: Context) : AppCompatTextView(context) {

    init {
        setTextColor(Color.GREEN)
        setBackgroundColor(Color.argb(150, 0, 0, 0))
        setPadding(20, 20, 20, 20)
        textSize = 14f
        gravity = Gravity.START

        translationX = 20f
        translationY = 40f
    }

    fun update(state: com.reconmaps.app.runtime.SystemState) {

        val self = state.vehicles.find { it.id == "SELF" }

        if (self != null) {

            val latStr = String.format("%.5f", self.x)
            val lonStr = String.format("%.5f", self.y)

            text = """
            GPS: $latStr, $lonStr
            GPS Status: ${if (state.gpsEnabled) "ON" else "OFF"}
            Transport: ${if (state.transportOnline) "ONLINE" else "OFFLINE"}
            Channel: ${state.channel.name}
        """.trimIndent()

        } else {
            text = "Waiting for GPS..."
        }

    }
}