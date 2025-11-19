package com.aradsheybak.pos_simulator_iso8583.data.datasource.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket

class ISOSocketDataSource {

    suspend fun sendMessage(
        host: String,
        port: Int,
        message: ByteArray
    ): ByteArray = withContext(Dispatchers.IO) {
        println("🔌 Attempting to connect to $host:$port")
        println("📤 Message to send: ${String(message)} (${message.size} bytes)")

        try {
            Socket(host, port).use { socket ->
                println("✅ Socket connected successfully")
                socket.soTimeout = 15000
                val output = socket.getOutputStream()

                // Send 2-byte length prefix
                val messageLength = message.size
                val lengthBytes = byteArrayOf(
                    (messageLength shr 8 and 0xFF).toByte(),
                    (messageLength and 0xFF).toByte()
                )

                println("📤 Sending length prefix: ${lengthBytes.size} bytes")
                output.write(lengthBytes)

                println("📤 Sending message: ${message.size} bytes")
                output.write(message)
                output.flush()

                println("✅ Message sent successfully")

                // Read response
                val input = socket.getInputStream()
                println("📥 Waiting for response...")

                val hi = input.read()
                val lo = input.read()
                println("📥 Received length bytes: hi=$hi, lo=$lo")

                if (hi == -1 || lo == -1) {
                    throw IOException("Server closed connection without response")
                }

                val responseLength = (hi and 0xFF shl 8) or (lo and 0xFF)
                println("📥 Response length: $responseLength bytes")

                if (responseLength <= 0) {
                    throw IOException("Invalid response length: $responseLength")
                }

                if (responseLength > 8192) {
                    throw IOException("Response too large: $responseLength")
                }

                val responseData = ByteArray(responseLength)
                var totalRead = 0

                while (totalRead < responseLength) {
                    val read = input.read(responseData, totalRead, responseLength - totalRead)
                    if (read == -1) {
                        throw IOException("Stream ended prematurely. Read $totalRead of $responseLength bytes")
                    }
                    totalRead += read
                    println("📥 Read $read bytes, total: $totalRead/$responseLength")
                }

                println("✅ Full response received: $totalRead bytes")
                println("📥 Response content: ${String(responseData)}")

                return@withContext responseData
            }
        } catch (e: Exception) {
            println("❌ Socket error: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            throw IOException("Socket communication failed: ${e.message}", e)
        }
    }
}