import dev.iso8583.server.HexUtils
import org.jpos.iso.ISOMsg
import java.net.Socket

object TestClient {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // استفاده از MessageFactory برای consistency
            val packager = MessageFactory.getPackager()

            val msg = ISOMsg()
            msg.packager = packager
            msg.setMTI("0200")
            msg.set(2, "4111111111111111")
            msg.set(3, "000000")
            msg.set(4, "000000001000") // مقدار 1000 ریال
            msg.set(11, "123456")
            msg.set(41, "12345678") // استفاده از عددی به جای متن
            msg.set(42, "123456789012345") // استفاده از عددی به جای متن

            val raw = msg.pack()
            println("Request HEX: ${HexUtils.toHex(raw)}")
            println("Request length: ${raw.size} bytes")

            Socket("127.0.0.1", 5000).use { socket ->
                socket.soTimeout = 10000
                val output = socket.getOutputStream()

                // ارسال طول و داده
                output.write(byteArrayOf((raw.size ushr 8).toByte(), raw.size.toByte()))
                output.write(raw)
                output.flush()

                println("Request sent successfully")

                val input = socket.getInputStream()
                val hi = input.read()
                val lo = input.read()
                if (hi == -1 || lo == -1) {
                    println("No response received")
                    return
                }

                val respLen = ((hi and 0xFF) shl 8) or (lo and 0xFF)
                println("Response length: $respLen bytes")

                val resp = ByteArray(respLen)
                var read = 0
                while (read < respLen) {
                    val r = input.read(resp, read, respLen - read)
                    if (r == -1) break
                    read += r
                }

                println("Response HEX: ${HexUtils.toHex(resp)}")

                // پارس کردن پاسخ با استفاده از MessageFactory
                try {
                    val respMsg = MessageFactory.parse(resp)
                    println("Response MTI: ${respMsg.getMTI()}")
                    println("Response Code (39): ${respMsg.getString(39)}")

                    // نمایش فیلدهای مهم پاسخ
                    for (i in listOf(2, 3, 4, 7, 11, 12, 13, 37, 39, 41, 42)) {
                        if (respMsg.hasField(i)) {
                            println("Field $i: ${respMsg.getString(i)}")
                        }
                    }
                } catch (e: Exception) {
                    println("Error parsing response: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("Client error: ${e.message}")
            e.printStackTrace()
        }
    }
}