package dev.iso8583.server

import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager
import java.io.InputStream


object MessageFactory {
    private val packager: GenericPackager

    init {
        val resource: InputStream = MessageFactory::class.java.getResourceAsStream("/iso8583-1987.xml") ?: throw IllegalStateException("Packager XML not found")
        packager = GenericPackager(resource)
    }

    fun parse(raw: ByteArray): ISOMsg {
        val msg = ISOMsg()
        msg.packager = packager
        msg.unpack(raw)
        return msg
    }

    fun buildResponse(message: ISOMsg): ByteArray {
        val resp = ISOMsg()
        resp.packager = packager

        val mti = message.getMTI()
        resp.mti = when (mti) {
            "0200" -> "0210"
            "0800" -> "0810"
            else -> "0210"
        }

        message.children.forEach { field ->
            if (message.hasField(field.toString())){
                val value = message.value
                resp.set(field.toString(),value.toString())
            }
        }
        resp.set(39, "00")

        return resp.pack()
    }

    fun toHex(msg: ISOMsg): String {
        val packed = msg.pack()
        return HexUtils.toHex(packed)
    }
}