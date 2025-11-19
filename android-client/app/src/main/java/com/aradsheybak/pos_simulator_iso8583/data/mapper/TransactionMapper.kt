package com.aradsheybak.pos_simulator_iso8583.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult
import org.jpos.iso.IFA_BITMAP
import org.jpos.iso.IFA_LLNUM
import org.jpos.iso.IFA_NUMERIC
import org.jpos.iso.ISOFieldPackager
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager

class TransactionMapper {

    // Create a simple packager for ISO8583
    private fun createSimplePackager(): GenericPackager {
        val packager = GenericPackager()
        val fieldPackagers = arrayOfNulls<ISOFieldPackager>(129)

        // Define basic fields
        fieldPackagers[0] = IFA_NUMERIC(4, "Message Type Indicator")
        fieldPackagers[1] = IFA_BITMAP(8, "Bitmap")
        fieldPackagers[2] = IFA_LLNUM(19, "Primary Account Number")
        fieldPackagers[3] = IFA_NUMERIC(6, "Processing Code")
        fieldPackagers[4] = IFA_NUMERIC(12, "Amount, Transaction")
        fieldPackagers[7] = IFA_NUMERIC(10, "Transmission Date and Time")
        fieldPackagers[11] = IFA_NUMERIC(6, "Systems Trace Audit Number")
        fieldPackagers[12] = IFA_NUMERIC(6, "Time, Local Transaction")
        fieldPackagers[13] = IFA_NUMERIC(4, "Date, Local Transaction")
        fieldPackagers[39] = IFA_NUMERIC(2, "Response Code")
        fieldPackagers[41] = IFA_NUMERIC(8, "Card Acceptor Terminal Identification")
        fieldPackagers[42] = IFA_NUMERIC(15, "Card Acceptor Identification Code")

        packager.setFieldPackager(fieldPackagers)
        return packager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapToISO8583Message(transaction: Transaction): ByteArray {
        return buildRealISOMessage(transaction)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildRealISOMessage(transaction: Transaction): ByteArray {
        val msg = ISOMsg()
        val packager = createSimplePackager()
        msg.packager = packager  // 🔑 این خط رو اضافه کن

        // Set MTI (Message Type Indicator)
        msg.setMTI("0200") // Financial transaction request

        // Set basic fields
        msg.set(2, transaction.pan) // Primary Account Number
        msg.set(3, "000000") // Processing Code (Purchase)
        msg.set(4, transaction.amount.padStart(12, '0')) // Amount
        msg.set(7, getTransmissionDateTime()) // Transmission date/time
        msg.set(11, getSTAN()) // Systems Trace Audit Number
        msg.set(41, transaction.terminalId) // Terminal ID
        msg.set(42, transaction.merchantId) // Merchant ID

        return msg.pack()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTransmissionDateTime(): String {
        val now = java.time.LocalDateTime.now()
        return now.format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmmss"))
    }

    private fun getSTAN(): String {
        return System.currentTimeMillis().toString().takeLast(6)
    }

    fun mapToTransactionResult(
        response: ByteArray,
        originalTransaction: Transaction
    ): TransactionResult {
        return try {
            val msg = ISOMsg()
            val packager = createSimplePackager()
            msg.packager = packager  // 🔑 این خط رو اضافه کن
            msg.unpack(response)

            TransactionResult(
                mti = msg.getMTI(),
                responseCode = msg.getString(39),
                responseMessage = getResponseMessage(msg.getString(39)),
                rawResponse = response.toHexString()
            )
        } catch (e: Exception) {
            TransactionResult(
                mti = "ERROR",
                responseCode = "99",
                responseMessage = "Failed to parse response: ${e.message}",
                rawResponse = String(response)
            )
        }
    }

    private fun getResponseMessage(code: String): String {
        return when (code) {
            "00" -> "Approved"
            "04" -> "Pick-up card"
            "51" -> "Insufficient funds"
            "13" -> "Invalid amount"
            "12" -> "Invalid transaction"
            else -> "Unknown response: $code"
        }
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}