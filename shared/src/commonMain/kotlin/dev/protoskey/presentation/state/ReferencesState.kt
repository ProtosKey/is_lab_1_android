package dev.protoskey.presentation.state

import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import dev.protoskey.presentation.model.Deletion
import dev.protoskey.presentation.model.Editor
import dev.protoskey.presentation.model.ReferenceTab

data class ReferencesState(
    val tab: ReferenceTab = ReferenceTab.COORDINATES,
    val coordinates: List<Coordinates> = emptyList(),
    val humans: List<Human> = emptyList(),
    val editor: Editor? = null,
    val deletion: Deletion? = null,
    val isLoading: Boolean = true
)
