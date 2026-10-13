package dev.protoskey.presentation.tools

import dev.protoskey.data.model.City
import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import kotlin.math.abs

object Labels {
    const val EMPTY = "—"

    fun coordinates(coordinates: Coordinates): String =
        "Широта ${coordinates.x}, долгота ${coordinates.y} (№${coordinates.id})"

    fun human(human: Human): String =
        "${human.name}, ${age(human.age)} (№${human.id})"

    fun city(city: City): String =
        "${city.name} (№${city.id}): ${city.population} ${plural(city.population, "житель", "жителя", "жителей")}"

    fun age(age: Long?): String = age?.let { "$it ${plural(it, "год", "года", "лет")}" } ?: EMPTY

    /** Склонение по числу: 1 город, 2 города, 5 городов. */
    fun plural(count: Number?, one: String, few: String, many: String): String {
        val rest = abs(count?.toLong() ?: 0) % 100
        return when {
            rest in 11..14 -> many
            rest % 10 == 1L -> one
            rest % 10 in 2..4 -> few
            else -> many
        }
    }

    fun value(value: Any?): String = when (value) {
        null -> EMPTY
        is Boolean -> if (value) "Да" else "Нет"
        is String -> value.ifBlank { EMPTY }
        else -> value.toString()
    }

    // Сервер присылает даты как 2026-10-02T18:59
    fun date(value: String?): String = value?.replace('T', ' ')?.ifBlank { null } ?: EMPTY
}
