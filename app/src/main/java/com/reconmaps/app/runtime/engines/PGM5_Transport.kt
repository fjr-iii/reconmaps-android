package com.reconmaps.app.runtime.engines
import com.reconmaps.app.runtime.engines.TransportPacket
import com.reconmaps.app.runtime.RuntimeShell

import android.util.Log

class PGM5_Transport {
    companion object {
        private const val TAG = "PGM5_TRANSPORT"
    }

    fun sendPacket(packet: TransportPacket) {
        Log.d(TAG, "[TRANSPORT] Sending packet via CELLULAR")

        Thread {
            try {
                val url = java.net.URL("http://192.168.1.194:3000/packet")
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
                    os.flush()
                }

                val responseCode = connection.responseCode
                Log.d(TAG, "[TRANSPORT] Response Code: $responseCode")


                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = stream?.bufferedReader()?.readText()
                Log.d(TAG, "[TRANSPORT] ResponseCode: $responseCode")
                Log.d(TAG, "[TRANSPORT] Response: $response")

                if (responseCode in 200..299 && response != null) {

                    val jsonArray = org.json.JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)

                        val incomingPacket = TransportPacket(
                            deviceId = obj.getString("deviceId"),
                            timestamp = obj.getLong("timestamp"),
                            lat = obj.getDouble("lat"),
                            lon = obj.getDouble("lon")
                        )

                        receivePacket(incomingPacket)
                    }

                } else {
                    Log.e(TAG, "[TRANSPORT] Bad response: $responseCode")
                }

                if (responseCode in 200..299) {
                    Log.d(TAG, "[TRANSPORT] Send success")
                } else {
                    Log.e(TAG, "[TRANSPORT] Send failed: HTTP $responseCode")
                }

            }

            catch (e: Exception) {
                Log.e(TAG, "[TRANSPORT] Send failed", e)
                e.printStackTrace()
            }

        }.start()
    }
    fun receivePacket(packet: TransportPacket) {
        Log.d(TAG, "[RECEIVE] Packet received: $packet")
        com.reconmaps.app.runtime.RuntimeShell.onPacketReceived(packet)
    }
}