package dev.iso8583.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.experimental.and

class ConnectionHandler(private val socket: Socket) {
    private val logger = LoggerFactory.getLogger(ConnectionHandler::class.java)
    private val processor = MessageProcessor()

    suspend fun handle() = withContext(Dispatchers.IO) {
        logger.info("Handling connection from ${socket.inetAddress.hostAddress}:${socket.port}")
        val input = DataInputStream(socket.getInputStream())
        val output = DataOutputStream(socket.getOutputStream())

        try {
            while (!socket.isClosed) {
                // خواندن header طول (2 بایت big-endian)
                val hi = tryReadByte(input) ?: break
                val lo = tryReadByte(input) ?: break
                val length = ((hi.toInt() and 0xFF) shl 8) or (lo.toInt() and 0xFF)
                if (length <= 0) {
                    logger.warn("Invalid message length: $length - closing connection")
                    break
                }

                // خواندن payload
                val payload = ByteArray(length)
                var read = 0
                while (read < length) {
                    val r = input.read(payload, read, length - read)
                    if (r == -1) {
                        throw RuntimeException("Stream closed while reading payload")
                    }
                    read += r
                }

                // پردازش پیام
                val responsePayload = processor.process(payload)

                // ارسال پاسخ با همان فریمینگ (2 بایت طول + payload)
                val respLen = responsePayload.size
                output.writeByte((respLen ushr 8) and 0xFF)
                output.writeByte(respLen and 0xFF)
                output.write(responsePayload)
                output.flush()
                logger.info("Sent response (len=$respLen) to ${socket.inetAddress.hostAddress}:${socket.port}")
            }
        } catch (e: Exception) {
            logger.error("Connection error: ${e.message}", e)
        } finally {
            try {
                socket.close()
            } catch (_: Exception) {}
            logger.info("Connection closed: ${socket.inetAddress.hostAddress}:${socket.port}")
        }
    }

    private fun tryReadByte(input: DataInputStream): Byte? {
        return try {
            input.readByte()
        } catch (e: Exception) {
            null
        }
    }
}