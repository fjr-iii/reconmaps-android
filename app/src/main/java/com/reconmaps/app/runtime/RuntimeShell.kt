package com.reconmaps.app.runtime

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

import com.reconmaps.app.runtime.engines.PGM1_GPS
import com.reconmaps.app.runtime.engines.PGM4_DataSync
import com.reconmaps.app.runtime.engines.PGM5_Transport

import com.reconmaps.app.runtime.trail.TrailManager

object RuntimeShell {

    val selfId: String = java.util.UUID.randomUUID().toString()

    // --------------------------------------------------
    // STATE
    // --------------------------------------------------
    private var state = SystemState(
        vehicles = emptyList(),
        gpsEnabled = true,
        transportOnline = true,
        channel = Channel.ALPHA
    )

    private val data = PGM4_DataSync(selfId)
    private val transport = PGM5_Transport()

    private val listeners = mutableListOf<(SystemState) -> Unit>()

    private val handler = Handler(Looper.getMainLooper())

    private val trailManager = TrailManager()

    private lateinit var gps: PGM1_GPS


    private var tick = 0


    // --------------------------------------------------
    // PUBLIC API
    // --------------------------------------------------
    fun start(context: Context) {
        Log.d("RUNTIME", "APP STARTED")

        gps = PGM1_GPS(context.applicationContext)

        gps.start()

        loop()
    }

    fun subscribe(listener: (SystemState) -> Unit) {
        listeners.add(listener)
        listener(state)
    }
    fun getTrailPoints() = trailManager.getTrailPoints()

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
        notifyListeners()
    }

    // --------------------------------------------------
    // MAIN LOOP
    // --------------------------------------------------
    private fun loop() {

        handler.postDelayed({

            tick++

            val lat = gps.lastGoodLat
            val lon = gps.lastGoodLon

            lat?.let { safeLat ->
                lon?.let { safeLon ->

                    Log.d("RUNTIME", "[GPS->RUNTIME] lat=$safeLat lon=$safeLon")

                    state.latitude = safeLat
                    state.longitude = safeLon

                    Log.d("RUNTIME", "[OUTBOUND] Updating self vehicle")

                    data.updateVehicle(
                        selfId,
                        safeLat,
                        safeLon,
                        System.currentTimeMillis(),
                        true
                    )

                    trailManager.addTrailPoint(
                        lat = safeLat,
                        lon = safeLon,
                        timestamp = System.currentTimeMillis()
                    )

                    transport.sendPacket(
                        com.reconmaps.app.runtime.engines.TransportPacket(
                            deviceId = selfId,
                            timestamp = System.currentTimeMillis(),
                            lat = safeLat,
                            lon = safeLon
                        )
                    )
                    val rawVehicles = data.getVehicles()

                    val sorted = rawVehicles.sortedBy { it.id }

                    // ❗ Exclude self BEFORE picking roles
                    val nonSelf = sorted.filter { !it.isSelf }

                    val leaderId = nonSelf.getOrNull(0)?.id
                    val sweepId = if (nonSelf.size > 1) nonSelf.last().id else null

                    val vehicles = sorted.map { vehicle ->

                        val role = when {

                            vehicle.isSelf -> ConvoyRole.SELF
                            vehicle.id == leaderId && vehicle.id != selfId -> ConvoyRole.LEADER
                            vehicle.id == sweepId  && vehicle.id != selfId -> ConvoyRole.SWEEP
                            else -> ConvoyRole.MEMBER
                        }
                        Log.d("ROLE_ASSIGN", "ID=${vehicle.id} ROLE=$role SELF=${vehicle.isSelf}")

                        vehicle.copy(convoyRole = role)
                    }

                    Log.d("RUNTIME", "VEHICLES SIZE = ${vehicles.size}")

                    state = state.copy(vehicles = vehicles)
                }
            }

            // --------------------------------------------------
            // INBOUND TRANSPORT
            // --------------------------------------------------

            pollInboundTransport()

            // --------------------------------------------------
            // UPDATE STATE
            // --------------------------------------------------



            // --------------------------------------------------
            // NOTIFY UI
            // --------------------------------------------------

            notifyListeners()

            // --------------------------------------------------
            // LOOP AGAIN
            // --------------------------------------------------

            loop()

        }, 1000)
    }

    // --------------------------------------------------
    // TRANSPORT INBOUND
    // --------------------------------------------------

    private fun pollInboundTransport() {

        Log.d("RUNTIME", "[INBOUND] pollInboundTransport() running")

        val packets = transport.drainInboundQueue()

        Log.d("RUNTIME", "[INBOUND] queue size: ${packets.size}")

        if (packets.isEmpty()) return

        for (packet in packets) {

            if (packet.deviceId == state.selfId) {
                Log.d("RUNTIME", "[INBOUND] skipping self packet")
                continue
            }

            Log.d("RUNTIME", "[INBOUND] processing packet: $packet")

            data.updateVehicle(
                packet.deviceId,
                packet.lat,
                packet.lon,
                packet.timestamp,
                packet.deviceId == state.selfId
            )
        }

        // push updates to UI
// push updates to UI
        notifyListeners()
    }

    // --------------------------------------------------
    // INTERNAL
    // --------------------------------------------------

    private fun notifyListeners() {
        listeners.forEach { it(state) }
    }
}















