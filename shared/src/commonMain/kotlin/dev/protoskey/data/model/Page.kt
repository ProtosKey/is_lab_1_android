package dev.protoskey.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val items: List<T> = emptyList(),
    val totalCount: Long = 0
)
