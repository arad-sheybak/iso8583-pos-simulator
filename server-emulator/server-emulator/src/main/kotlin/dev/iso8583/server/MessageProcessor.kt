package dev.iso8583.server

import org.jpos.iso.ISOMsg
import org.slf4j.LoggerFactory

class MessageProcessor {
    private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)

    fun process(payload: ByteArray): ByteArray {
        logger.info("Received raw payload (${payload.size} bytes)")
        logger.info("Payload HEX: ${HexUtils.toHex(payload)}")

        val isoMessage: ISOMsg = try {
            MessageFactory.parse(payload)
        } catch (e: Exception) {
            logger.error("Failed to parse ISO message: ${e.message}")
            logger.debug("Parsing error details:", e)
            return buildErrorResponse("96", "Message parsing error")
        }

        MessageFactory.logMessage(isoMessage, "INCOMING")

        val mti = isoMessage.getMTI()
        logger.info("Processing message with MTI: $mti")

        val responseBytes: ByteArray = try {
            when (mti) {
                "0800" -> processNetworkMessage(isoMessage)
                "0200", "0100" -> processFinancialMessage(isoMessage)
                "0400" -> processReversalMessage(isoMessage)
                else -> processUnknownMessage(isoMessage)
            }
        } catch (e: Exception) {
            logger.error("Error processing message MTI $mti: ${e.message}")
            logger.debug("Processing error details:", e)
            buildErrorResponse("96", "Processing error")
        }

        try {
            val responseMsg = MessageFactory.parse(responseBytes)
            MessageFactory.logMessage(responseMsg, "OUTGOING")
        } catch (e: Exception) {
            logger.info("Response HEX: ${HexUtils.toHex(responseBytes)}")
        }

        logger.info("Successfully processed MTI: $mti")
        return responseBytes
    }

    private fun processNetworkMessage(message: ISOMsg): ByteArray {
        logger.info("Processing network management message")

        if (message.hasField(70)) {
            val networkCode = message.getString(70)
            logger.info("Network management code: $networkCode")

            return when (networkCode) {
                "001" -> {
                    logger.info("Sign-on request")
                    MessageFactory.createSignOnResponse()
                }
                "002" -> {
                    logger.info("Sign-off request")
                    MessageFactory.createSignOffResponse()
                }
                "301" -> {
                    logger.info("Echo test request")
                    MessageFactory.createEchoResponse()
                }
                else -> {
                    logger.warn("Unknown network management code: $networkCode")
                    MessageFactory.buildResponse(message)
                }
            }
        }

        return MessageFactory.buildResponse(message)
    }

    private fun processFinancialMessage(message: ISOMsg): ByteArray {
        logger.info("Processing financial transaction message")

        val validationResult = validateFinancialMessage(message)
        if (validationResult != null) {
            logger.warn("Validation failed: ${validationResult.second}")
            return buildErrorResponse(validationResult.first, validationResult.second)
        }

        try {
            val pan = if (message.hasField(2)) message.getString(2) else "N/A"
            val amount = if (message.hasField(4)) message.getString(4) else "N/A"
            val processingCode = if (message.hasField(3)) message.getString(3) else "N/A"

            logger.info("Transaction details - PAN: $pan, Amount: $amount, ProcessingCode: $processingCode")

            simulateTransactionProcessing(message)

            return MessageFactory.buildResponse(message)

        } catch (e: Exception) {
            logger.error("Error in financial transaction processing: ${e.message}")
            return buildErrorResponse("96", "Transaction processing error")
        }
    }

    private fun processReversalMessage(message: ISOMsg): ByteArray {
        logger.info("Processing reversal message")

        val response = MessageFactory.buildResponse(message)

        try {
            val responseMsg = MessageFactory.parse(response)
            if (responseMsg.getString(39) == "00") {
                logger.info("Reversal processed successfully")
            }
        } catch (e: Exception) {
            logger.debug("Could not parse reversal response for logging")
        }

        return response
    }

    private fun processUnknownMessage(message: ISOMsg): ByteArray {
        logger.warn("Unknown message type: ${message.getMTI()}")

        return buildErrorResponse("94", "Unknown message type")
    }

    private fun validateFinancialMessage(message: ISOMsg): Pair<String, String>? {
        if (!message.hasField(2)) {
            return Pair("02", "PAN required")
        }

        if (!message.hasField(3)) {
            return Pair("03", "Processing code required")
        }

        if (!message.hasField(4)) {
            return Pair("13", "Amount required")
        }

        if (!message.hasField(11)) {
            return Pair("12", "STAN required")
        }

        if (message.hasField(2)) {
            val pan = message.getString(2)
            if (pan.length < 13 || pan.length > 19 || !pan.all { it.isDigit() }) {
                return Pair("02", "Invalid PAN format")
            }
        }

        if (message.hasField(4)) {
            val amountStr = message.getString(4)
            try {
                val amount = amountStr.toLong()
                if (amount <= 0) {
                    return Pair("13", "Invalid transaction amount")
                }
            } catch (e: NumberFormatException) {
                return Pair("13", "Invalid amount format")
            }
        }

        return null
    }

    private fun simulateTransactionProcessing(message: ISOMsg) {
        try {
            Thread.sleep(50)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        if (message.getMTI() == "0100" || message.getMTI() == "0200") {
            logger.debug("Simulating transaction processing...")
        }
    }

    private fun buildErrorResponse(responseCode: String, reason: String): ByteArray {
        logger.warn("Building error response: $responseCode - $reason")

        val errorMsg = ISOMsg()
        errorMsg.packager = MessageFactory.getPackager()

        errorMsg.setMTI("0810")
        errorMsg.set(39, responseCode)

        try {
            errorMsg.set(44, reason.take(25))
        } catch (e: Exception) {
            logger.debug("Could not set field 44: ${e.message}")
        }

        setBasicDateTimeFields(errorMsg)

        return errorMsg.pack()
    }

    private fun setBasicDateTimeFields(msg: ISOMsg) {
        val currentTime = java.time.LocalDateTime.now()

        try {
            val transmissionDateTime = currentTime.format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmmss"))
            msg.set(7, transmissionDateTime)
        } catch (e: Exception) {
            logger.debug("Could not set field 7: ${e.message}")
        }

        try {
            val localTime = currentTime.format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"))
            msg.set(12, localTime)
        } catch (e: Exception) {
            logger.debug("Could not set field 12: ${e.message}")
        }

        try {
            val localDate = currentTime.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"))
            msg.set(13, localDate)
        } catch (e: Exception) {
            logger.debug("Could not set field 13: ${e.message}")
        }
    }

    fun healthCheck(): Boolean {
        return try {
            val testMsg = ISOMsg()
            testMsg.packager = MessageFactory.getPackager()
            testMsg.setMTI("0800")
            testMsg.set(70, "301")
            val packed = testMsg.pack()
            packed.isNotEmpty()
        } catch (e: Exception) {
            logger.error("Health check failed: ${e.message}")
            false
        }
    }

    fun isValidResponse(responseBytes: ByteArray): Boolean {
        return try {
            MessageFactory.parse(responseBytes)
            true
        } catch (e: Exception) {
            false
        }
    }
}