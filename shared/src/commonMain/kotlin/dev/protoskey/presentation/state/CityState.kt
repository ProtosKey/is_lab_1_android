package dev.protoskey.presentation.state

import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import dev.protoskey.presentation.model.CityForm

data class CityState(
    val id: Int? = null,
    val form: CityForm = CityForm(),
    val creationDate: String? = null,
    val coordinates: List<Coordinates> = emptyList(),
    val humans: List<Human> = emptyList(),
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isClosed: Boolean = false
)
