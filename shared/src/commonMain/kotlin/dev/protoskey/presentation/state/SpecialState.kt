package dev.protoskey.presentation.state

import dev.protoskey.data.model.City

data class SpecialState(
    val climate: String? = null,
    val meters: String = "",
    val namePart: String = "",
    val sourceId: String = "",
    val targetId: String = "",
    val smallestSourceId: String = "",
    val count: Long? = null,
    val found: List<City>? = null,
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)
