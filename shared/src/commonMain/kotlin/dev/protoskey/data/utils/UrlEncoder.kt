package dev.protoskey.data.utils

object UrlEncoder {
    private const val UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
    private const val HEX = "0123456789ABCDEF"

    fun encode(value: String): String = buildString {
        value.encodeToByteArray().forEach { byte ->
            val code = byte.toInt() and 0xFF
            if (code.toChar() in UNRESERVED) {
                append(code.toChar())
            } else {
                append('%').append(HEX[code shr 4]).append(HEX[code and 0x0F])
            }
        }
    }

    fun query(vararg params: Pair<String, Any?>): String = params
        .filter { (_, value) -> value != null }
        .joinToString(separator = "&", prefix = "?") { (key, value) -> "$key=${encode(value.toString())}" }
}
