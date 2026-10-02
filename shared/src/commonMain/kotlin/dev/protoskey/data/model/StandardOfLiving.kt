package dev.protoskey.data.model

enum class StandardOfLiving(val label: String) {
    VERY_HIGH("Очень высокий"),
    HIGH("Высокий"),
    NIGHTMARE("Кошмарный");

    companion object {
        fun labelOf(name: String?): String = entries.find { it.name == name }?.label ?: "—"
    }
}
