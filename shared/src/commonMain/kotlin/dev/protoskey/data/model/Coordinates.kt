package dev.protoskey.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val id: Int? = null,
    val x: Int? = null,
    val y: Int? = null
)
