package com.reconmaps.app.runtime

import android.os.Handler
import android.os.Looper
import com.reconmaps.app.runtime.engines.PGM1_GPS
import com.reconmaps.app.runtime.engines.PGM4_DataSync
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

    private val data = PGM4_DataSync()
    private val selfId = java.util.UUID.randomUUID().toString()

    private val transport = PGM5_Transport()

    private val listeners = mutableListOf<(SystemState) -> Unit>()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var gps: PGM1_GPS
    private var tick = 0
    private var lastSendTime: Long = 0

    fun start(context: android.content.Context) {

        android.util.Log.d("TEST", "APP STARTED")

        gps = PGM1_GPS(context) { lat, lon ->

            android.util.Log.d("RUNTIME", "[GPS->RUNTIME] lat=$lat lon=$lon")

            data.updateVehicle(
                "SELF",
                lat,
                lon,
                state.channel,
                System.currentTimeMillis()
            )

            val now = System.currentTimeMillis()

            if (now - lastSendTime > 2000) {   // 🔴 send every 2 seconds

                val packet = TransportPacket(
                    deviceId = selfId,
                    timestamp = now,
                    lat = lat.toDouble(),
                    lon = lon.toDouble()
                )

                transport.sendPacket(packet)

                lastSendTime = now
            }

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

            // 🔕 No simulation, no overrides

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
    fun onPacketReceived(packet: TransportPacket) {
        android.util.Log.d("RUNTIME", "[INBOUND] Packet received from transport: $packet")

        data.updateVehicle(
            packet.deviceId,
            packet.lat.toFloat(),
            packet.lon.toFloat(),
            state.channel,
            packet.timestamp
        )

        refreshState()
    }
}