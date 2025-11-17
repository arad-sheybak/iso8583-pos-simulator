package dev.iso8583.server

object HexUtils {
    private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
    fun toHex(bytes: ByteArray): String {
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach { b ->
            val i = b.toInt()
            result.append(HEX_CHARS[(i ushr 4) and 0x0F])
            result.append(HEX_CHARS[i and 0x0F])
        }
        return result.toString()
    }

    fun fromHex(hex: String): ByteArray {
        val clean = hex.replace("\\s".toRegex(), "")
        require(clean.length % 2 == 0) { "Hex string must have even length" }
        val result = ByteArray(clean.length / 2)
        for (i in result.indices) {
            val index = i * 2
            val j = clean.substring(index, index + 2).toInt(16)
            result[i] = j.toByte()
        }
        return result
    }
}