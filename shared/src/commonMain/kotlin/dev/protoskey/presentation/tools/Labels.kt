package dev.protoskey.presentation.tools

import dev.protoskey.data.model.City
import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human

object Labels {
    const val EMPTY = "—"

    fun coordinates(coordinates: Coordinates): String =
        "№${coordinates.id}: широта ${coordinates.x}, долгота ${coordinates.y}"

    fun human(human: Human): String =
        "№${human.id}: ${human.name}, ${human.age} лет"

    fun city(city: City): String =
        "№${city.id}: ${city.name}, население ${city.population}"

    fun value(value: Any?): String = when (value) {
        null -> EMPTY
        is Boolean -> if (value) "Да" else "Нет"
        is String -> value.ifBlank { EMPTY }
        else -> value.toString()
    }

    // Сервер присылает даты как 2026-10-02T18:59
    fun date(value: String?): String = value?.replace('T', ' ')?.ifBlank { null } ?: EMPTY
}
