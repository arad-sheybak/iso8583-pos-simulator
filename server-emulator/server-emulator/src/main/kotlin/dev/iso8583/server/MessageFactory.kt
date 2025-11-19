import dev.iso8583.server.HexUtils
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager

object MessageFactory {
    private val packager: GenericPackager

    init {
        packager = createSimplePackager()
    }

    private fun createSimplePackager(): GenericPackager {
        val packager = GenericPackager()

        val maxField = 128
        val fieldPackagers = arrayOfNulls<org.jpos.iso.ISOFieldPackager>(maxField + 1)

        fieldPackagers[0] = org.jpos.iso.IFA_NUMERIC(4, "Message Type Indicator")
        fieldPackagers[1] = org.jpos.iso.IFA_BITMAP(8, "Bitmap")
        fieldPackagers[2] = org.jpos.iso.IFA_LLNUM(19, "Primary Account Number")
        fieldPackagers[3] = org.jpos.iso.IFA_NUMERIC(6, "Processing Code")
        fieldPackagers[4] = org.jpos.iso.IFA_NUMERIC(12, "Amount, Transaction")
        fieldPackagers[7] = org.jpos.iso.IFA_NUMERIC(10, "Transmission Date and Time")
        fieldPackagers[11] = org.jpos.iso.IFA_NUMERIC(6, "Systems Trace Audit Number")
        fieldPackagers[12] = org.jpos.iso.IFA_NUMERIC(6, "Time, Local Transaction")
        fieldPackagers[13] = org.jpos.iso.IFA_NUMERIC(4, "Date, Local Transaction")
        fieldPackagers[39] = org.jpos.iso.IFA_NUMERIC(2, "Response Code")
        fieldPackagers[41] = org.jpos.iso.IFA_NUMERIC(8, "Card Acceptor Terminal Identification")
        fieldPackagers[42] = org.jpos.iso.IFA_NUMERIC(15, "Card Acceptor Identification Code")
        fieldPackagers[70] = org.jpos.iso.IFA_NUMERIC(3, "Network Management Information Code")

        packager.setFieldPackager(fieldPackagers)
        return packager
    }

    fun getPackager(): GenericPackager = packager

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
        resp.setMTI(when (mti) {
            "0200" -> "0210"
            "0800" -> "0810"
            "0100" -> "0110"
            else -> "0210"
        })

        val fieldsToCopy = listOf(2, 3, 4, 7, 11, 12, 13, 41, 42)

        fieldsToCopy.forEach { fieldNum ->
            if (message.hasField(fieldNum)) {
                try {
                    val fieldValue = message.getString(fieldNum)
                    resp.set(fieldNum, fieldValue)
                } catch (e: Exception) {
                }
            }
        }

        resp.set(39, determineResponseCode(message))

        return resp.pack()
    }

    private fun determineResponseCode(message: ISOMsg): String {
        val mti = message.getMTI()

        if (mti == "0800") {
            return "00"
        }

        if (message.hasField(2)) {
            val pan = message.getString(2)
            val blacklistedPans = listOf("4000000000000002", "5111111111111118")
            if (blacklistedPans.contains(pan)) {
                return "04"
            }
        }

        if (message.hasField(4)) {
            val amountStr = message.getString(4)
            try {
                val amount = amountStr.toLong()
                if (amount > 1000000) return "51"
                if (amount <= 0) return "13"
            } catch (e: NumberFormatException) {
                return "12"
            }
        }

        return "00"
    }

    fun createEchoResponse(): ByteArray {
        val echoMsg = ISOMsg()
        echoMsg.packager = packager
        echoMsg.setMTI("0810")
        echoMsg.set(39, "00")
        return echoMsg.pack()
    }

    fun createSignOnResponse(): ByteArray {
        val signOnMsg = ISOMsg()
        signOnMsg.packager = packager
        signOnMsg.setMTI("0810")
        signOnMsg.set(39, "00")
        try {
            signOnMsg.set(70, "001")
        } catch (e: Exception) {
        }
        return signOnMsg.pack()
    }

    fun createSignOffResponse(): ByteArray {
        val signOffMsg = ISOMsg()
        signOffMsg.packager = packager
        signOffMsg.setMTI("0810")
        signOffMsg.set(39, "00")
        try {
            signOffMsg.set(70, "002")
        } catch (e: Exception) {
        }
        return signOffMsg.pack()
    }

    fun toHex(msg: ISOMsg): String {
        val packed = msg.pack()
        return HexUtils.toHex(packed)
    }

    fun logMessage(msg: ISOMsg, direction: String) {
        val logger = org.slf4j.LoggerFactory.getLogger(MessageFactory::class.java)
        logger.info("$direction Message - MTI: ${msg.getMTI()}")

        for (i in listOf(2, 3, 4, 7, 11, 12, 13, 39, 41, 42, 70)) {
            if (msg.hasField(i)) {
                try {
                    val value = msg.getString(i)
                    logger.info("  Field $i: $value")
                } catch (e: Exception) {
                    logger.info("  Field $i: [Unable to read value]")
                }
            }
        }

        logger.info("Full Message HEX: ${toHex(msg)}")
    }
}