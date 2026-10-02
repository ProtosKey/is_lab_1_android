package dev.protoskey.presentation.model

data class Deletion(
    val tab: ReferenceTab,
    val id: Int,
    val title: String,
    val linkedCities: Int,
    val candidates: List<Pair<Int, String>>,
    val replacementId: Int? = null
)
