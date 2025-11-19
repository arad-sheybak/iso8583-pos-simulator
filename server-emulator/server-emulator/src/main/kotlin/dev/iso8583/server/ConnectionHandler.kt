import dev.iso8583.server.MessageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.Socket

class ConnectionHandler(private val socket: Socket) {
    private val logger = LoggerFactory.getLogger(ConnectionHandler::class.java)
    private val processor = MessageProcessor()

    suspend fun handle() = withContext(Dispatchers.IO) {
        logger.info("Handling connection from ${socket.inetAddress.hostAddress}:${socket.port}")

        try {
            socket.soTimeout = 30000
            val input = socket.getInputStream()
            val output = socket.getOutputStream()

            while (!socket.isClosed && socket.isConnected) {
                if (input.available() > 0) {
                    val hi = input.read()
                    if (hi == -1) break
                    val lo = input.read()
                    if (lo == -1) break

                    val length = ((hi and 0xFF) shl 8) or (lo and 0xFF)
                    if (length <= 0 || length > 8192) {
                        logger.warn("Invalid message length: $length")
                        break
                    }

                    val payload = ByteArray(length)
                    var read = 0
                    while (read < length) {
                        val r = input.read(payload, read, length - read)
                        if (r == -1) {
                            throw IOException("Stream closed while reading payload")
                        }
                        read += r
                    }

                    logger.info("Received message (len=$length)")
                    val responsePayload = processor.process(payload)

                    val respLen = responsePayload.size
                    output.write(byteArrayOf((respLen ushr 8).toByte(), respLen.toByte()))
                    output.write(responsePayload)
                    output.flush()
                    logger.info("Sent response (len=$respLen)")
                } else {
                    delay(100)
                }
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
}