package dev.protoskey.view.feature.city.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.view.component.Section

@Composable
fun Related(
    creationDate: String?,
    coordinates: Coordinates?,
    governor: Human?
) {
    Section(title = "Подробнее") {
        Line("Добавлен в систему", Labels.date(creationDate))
        Line(
            "Координаты",
            coordinates?.let(Labels::coordinates) ?: Labels.EMPTY
        )
        Line(
            "Губернатор",
            governor?.let(Labels::human) ?: Labels.EMPTY
        )
        Line("Дата рождения губернатора", Labels.date(governor?.birthday))
    }
}

@Composable
private fun Line(label: String, value: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(text = value, style = MaterialTheme.typography.bodyLarge)
}
