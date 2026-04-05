package com.reconmaps.app.runtime

import android.os.Handler
import android.os.Looper
import com.reconmaps.app.runtime.engines.PGM1_GPS
import com.reconmaps.app.runtime.engines.PGM4_Data
import kotlin.math.cos
import kotlin.math.sin
import com.reconmaps.app.runtime.engines.PGM5_Transport
import com.reconmaps.app.runtime.engines.TransportPacket

object RuntimeShell {

    private var state = SystemState(
        vehicles = emptyList(),
        gpsEnabled = true,
        transportOnline = true,
        channel = Channel.ALPHA
    )

    private val data = PGM4_Data()
    private val selfId = "SELF"
    private val transport = PGM5_Transport()

    private val listeners = mutableListOf<(SystemState) -> Unit>()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var gps: PGM1_GPS
    private var tick = 0

    fun start(context: android.content.Context) {

        gps = PGM1_GPS(context) { x, y ->

            data.updateVehicle(selfId, x, y, state.channel)

            val packet = TransportPacket(
                deviceId = selfId,
                timestamp = System.currentTimeMillis(),
                lat = x.toDouble(),
                lon = y.toDouble()
            )

            android.util.Log.d("GPS_CHECK", "Sending: lat=$x lon=$y")

            transport.sendPacket(packet)

            refreshState()
        }

        gps.start()

        loop()
    }

    private fun refreshState() {
        state = state.copy(
            vehicles = data.getVehicles()
        )

        notifyListeners()
    }

    private fun loop() {
        handler.postDelayed({

            tick++

            // 🔄 Simulated vehicles (still OK)
            val t = System.currentTimeMillis() / 1000.0

            data.updateVehicle(
                "V1",
                (300 + 150 * cos(t)).toFloat(),
                (300 + 150 * sin(t)).toFloat(),
                Channel.ALPHA
            )

            data.updateVehicle(
                "V2",
                (500 + 100 * cos(t * 0.7)).toFloat(),
                (400 + 100 * sin(t * 0.7)).toFloat(),
                Channel.BRAVO
            )

            // ❗ DO NOT override state here anymore

            refreshState()

            loop()

        }, 1000)
    }

    fun subscribe(listener: (SystemState) -> Unit) {
        listeners.add(listener)
        listener(state)
    }

    private fun notifyListeners() {
        listeners.forEach { it(state) }
    }
    fun toggleGps() {
        state = state.copy(
            gpsEnabled = !state.gpsEnabled
        )
        notifyListeners()
    }

    fun toggleTracking() {
        state = state.copy(
            transportOnline = !state.transportOnline
        )
        notifyListeners()
    }
    fun nextChannel() {
        val next = when (state.channel) {
            Channel.ALPHA -> Channel.BRAVO
            Channel.BRAVO -> Channel.CHARLIE
            Channel.CHARLIE -> Channel.DELTA
            Channel.DELTA -> Channel.E11
            Channel.E11 -> Channel.ALPHA
        }

        state = state.copy(channel = next)

    }
}