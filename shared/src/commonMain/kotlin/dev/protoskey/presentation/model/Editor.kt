package dev.protoskey.presentation.model

sealed interface Editor {
    val id: Int?
    val errors: Map<String, String>

    data class CoordinatesEditor(
        override val id: Int? = null,
        val x: String = "",
        val y: String = "",
        override val errors: Map<String, String> = emptyMap()
    ) : Editor

    data class HumanEditor(
        override val id: Int? = null,
        val name: String = "",
        val age: String = "",
        val birthday: String = "",
        override val errors: Map<String, String> = emptyMap()
    ) : Editor
}
