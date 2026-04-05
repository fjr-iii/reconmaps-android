package com.reconmaps.app.runtime.engines
import com.reconmaps.app.runtime.engines.TransportPacket

import android.util.Log

class PGM5_Transport {

    companion object {
        private const val TAG = "PGM5_TRANSPORT"
    }

    fun sendPacket(packet: TransportPacket) {
        Log.d(TAG, "[TRANSPORT] Sending packet via CELLULAR")

        Thread {
            try {
                val url = java.net.URL("http://10.0.2.2:3000/packet")
                val connection = url.openConnection() as java.net.HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val json = """
                {
                    "deviceId": "${packet.deviceId}",
                    "timestamp": ${packet.timestamp},
                    "lat": ${packet.lat},
                    "lon": ${packet.lon}
                }
            """.trimIndent()

                Log.d(TAG, "[TRANSPORT] Payload: $json")

                connection.outputStream.use { os ->
                    os.write(json.toByteArray())
                }

                val responseCode = connection.responseCode

                if (responseCode in 200..299) {
                    Log.d(TAG, "[TRANSPORT] Send success")
                } else {
                    Log.e(TAG, "[TRANSPORT] Send failed: HTTP $responseCode")
                }

            } catch (e: Exception) {
                Log.e(TAG, "[TRANSPORT] Send failed: ${e.message}")
            }
        }.start()
    }
}