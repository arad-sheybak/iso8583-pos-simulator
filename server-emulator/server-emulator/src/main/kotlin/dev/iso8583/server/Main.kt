package dev.iso8583.server

import ConnectionHandler
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.net.ServerSocket

fun main() = runBlocking {
    val logger = LoggerFactory.getLogger("Main")
    logger.info("Starting ISO8583 Server Emulator...")
//    startSimpleHttpServer()
    // load config (from resources/config.yml). For dev, we read from working dir resources path
    val cfgFile = File("src/main/resources/config.yml")
    val mapper = YAMLMapper()
    val cfg = if (cfgFile.exists()) mapper.readTree(cfgFile) else mapper.createObjectNode()
    val port = cfg.path("server").path("port").asInt(5000)

    val serverSocket = ServerSocket(port)
    logger.info("Listening on port $port")

    try {
        while (true) {
            val socket = serverSocket.accept()
            logger.info("Accepted ${socket.inetAddress.hostAddress}:${socket.port}")
            launch(Dispatchers.IO) {
                val handler = ConnectionHandler(socket)
                handler.handle()
            }
        }
    } finally {
        try { serverSocket.close() } catch (_: Exception) {}
    }
}