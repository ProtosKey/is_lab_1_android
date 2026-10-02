package dev.protoskey.data.model

enum class Climate(val label: String) {
    RAIN_FOREST("Тропический лес"),
    TUNDRA("Тундра"),
    POLAR_ICECAP("Полярная шапка");

    companion object {
        fun labelOf(name: String?): String = entries.find { it.name == name }?.label ?: "—"
    }
}
