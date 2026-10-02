package dev.protoskey.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Human(
    val id: Int? = null,
    val name: String? = null,
    val age: Long? = null,
    val birthday: String? = null
)
