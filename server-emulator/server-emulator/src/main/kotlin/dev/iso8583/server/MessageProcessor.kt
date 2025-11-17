package dev.iso8583.server

import org.jpos.iso.ISOMsg
import org.slf4j.LoggerFactory

class MessageProcessor {
    private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)

    fun process(payload: ByteArray): ByteArray {
        val isoMessage: ISOMsg = try {
            MessageFactory.parse(payload)
        } catch (e: Exception) {
            logger.error("Failed to parse message", e)
            return payload
        }

        logger.info("Parsed ISO Message: MTI=${isoMessage.getMTI()}")
        isoMessage.children.forEach { field->
            if (isoMessage.hasField(field.toString())){
                logger.info(" Field #$field = ${isoMessage.value.toString()}")

            }
        }


        val responseBytes: ByteArray = try {
            MessageFactory.buildResponse(isoMessage)
        } catch (e: Exception) {
            logger.error("Failed to build response message", e)
            return payload
        }

        val responseHex = HexUtils.toHex(responseBytes)
        logger.info("Response (HEX): $responseHex")

        return responseBytes
    }
}