package dev.protoskey.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val login: String, val password: String)

@Serializable
data class Account(val login: String)

@Serializable
data class Count(val count: Long)

@Serializable
data class ErrorBody(
    val message: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)
