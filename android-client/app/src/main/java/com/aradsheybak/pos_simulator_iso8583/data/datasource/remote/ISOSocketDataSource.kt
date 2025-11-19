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
        try {
            Socket(host, port).use { socket ->
                socket.soTimeout = 10000
                val output = socket.getOutputStream()

                // Send message length (2 bytes) + data
                output.write(byteArrayOf((message.size shr 8).toByte(), message.size.toByte()))
                output.write(message)
                output.flush()

                // Read response
                val input = socket.getInputStream()
                val hi = input.read()
                val lo = input.read()
                if (hi == -1 || lo == -1) throw IOException("No response received")

                val responseLength = ((hi and 0xFF) shl 8) or (lo and 0xFF)
                ByteArray(responseLength).apply {
                    var read = 0
                    while (read < responseLength) {
                        read += input.read(this, read, responseLength - read)
                    }
                }
            }
        } catch (e: Exception) {
            throw IOException("Socket communication failed: ${e.message}", e)
        }
    }
}