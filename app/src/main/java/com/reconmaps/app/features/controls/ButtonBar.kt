package com.reconmaps.app.features.controls

import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import com.reconmaps.app.runtime.SystemState
import com.reconmaps.app.runtime.Channel

class ButtonBar(
    context: Context,
    val systemState: SystemState,
    val onGpsToggle: () -> Unit,
    val onTrackingToggle: () -> Unit,
    val onChannelNext: () -> Unit
) : LinearLayout(context) {

    init {
        // 🔴 DEBUG: make container visible
        setBackgroundColor(0xFFFF0000.toInt()) // keep for now (debug)

        setPadding(8, 8, 8, 8)

        orientation = VERTICAL

        val gpsBtn = Button(context).apply {
            text = "GPS"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(8, 8, 8, 8)
            }

            setBackgroundColor(0xFFFFFFFF.toInt())

            setOnClickListener { onGpsToggle() }
        }

        val trackingBtn = Button(context).apply {
            text = "TRACK"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(8, 8, 8, 8)
            }

            setBackgroundColor(0xFFFFFFFF.toInt())

            setOnClickListener { onTrackingToggle() }
        }

        val channelBtn = Button(context).apply {
            text = "CHANNEL: ${systemState.channel.name}"

            setBackgroundColor(
                when (systemState.channel) {
                    Channel.ALPHA -> 0xFF2196F3.toInt()   // blue
                    Channel.BRAVO -> 0xFF4CAF50.toInt()   // green
                    Channel.CHARLIE -> 0xFFFF9800.toInt() // orange
                    Channel.DELTA -> 0xFFF44336.toInt()   // red
                    Channel.E11 -> 0xFF9C27B0.toInt()     // purple
                }
            )

            setOnClickListener { onChannelNext() }
        }

        val cameraBtn = Button(context).apply {
            text = "CAMERA"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val routeBtn = Button(context).apply {
            text = "ROUTE"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val layersBtn = Button(context).apply {
            text = "LAYERS"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val settingsBtn = Button(context).apply {
            text = "SETTINGS"

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val row1 = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }

        val row2 = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }

// Add buttons to row 1
        row1.addView(gpsBtn)
        row1.addView(trackingBtn)
        row1.addView(cameraBtn)

// Add buttons to row 2
        row2.addView(routeBtn)
        row2.addView(layersBtn)
        row2.addView(settingsBtn)

// Add rows to container
        addView(row1)
        addView(row2)
    }
}