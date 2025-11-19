package dev.iso8583.server

import java.net.ServerSocket

fun startSimpleHttpServer() {
    Thread {
        try {
            val httpServer = ServerSocket(8080)
            println("HTTP Test Server started on http://192.168.1.162:8080")

            while (true) {
                val client = httpServer.accept()
                Thread {
                    val output = client.getOutputStream()
                    val response = """
                        HTTP/1.1 200 OK
                        Content-Type: text/plain
                        
                        ISO8583 Server is Running! ✅
                        
                        Server: 192.168.1.162
                        ISO8583 Port: 5000
                        Test Time: ${java.time.LocalDateTime.now()}
                    """.trimIndent()

                    output.write(response.toByteArray())
                    output.flush()
                    client.close()
                }.start()
            }
        } catch (e: Exception) {
            println("HTTP Server error: ${e.message}")
        }
    }.start()
}