package dev.iso8583.server

import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager
import java.net.Socket
import kotlin.jvm.java


    fun main() {
        val packager = GenericPackager(TestClient::class.java.getResourceAsStream("/iso8583-1987.xml"))

        val msg = ISOMsg()
        msg.packager = packager
        msg.mti = "0200"
        msg.set(2, "4000000000000002")
        msg.set(3, "000000")
        msg.set(4, "000000001000")    // مبلغ (مثال 10.00)
        msg.set(11, "123456")
        msg.set(41, "TERM0001")
        msg.set(42, "MERCH0001")

        val raw = msg.pack()
        val socket = Socket("127.0.0.1", 5000)
        socket.getOutputStream().use { out ->
            // فریمینگ 2-byte length
            out.write((raw.size ushr 8) and 0xFF)
            out.write(raw.size and 0xFF)
            out.write(raw)
            out.flush()

            val input = socket.getInputStream()
            val hi = input.read()
            val lo = input.read()
            val respLen = (hi shl 8) or lo
            val resp = ByteArray(respLen)
            input.read(resp)
            println("Response HEX: ${HexUtils.toHex(resp)}")
        }
    }
