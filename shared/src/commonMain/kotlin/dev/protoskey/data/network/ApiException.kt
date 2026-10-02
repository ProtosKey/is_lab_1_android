package dev.protoskey.data.network

class ApiException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    val code: Int = 0
) : Exception(message)
