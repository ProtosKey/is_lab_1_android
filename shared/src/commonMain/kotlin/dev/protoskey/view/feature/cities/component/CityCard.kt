package dev.protoskey.view.feature.cities.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.protoskey.data.model.City
import dev.protoskey.data.model.Climate
import dev.protoskey.data.model.StandardOfLiving
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.component.MetricRow

@Composable
fun CityCard(
    city: City,
    onOpen: () -> Unit
) {
    val isCapital = city.capital == true
    val color = if (isCapital) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    val border = if (isCapital) {
        BorderStroke(
            LocalAppDimens.current.strokeThick,
            MaterialTheme.colorScheme.primary
        )
    } else {
        BorderStroke(
            LocalAppDimens.current.strokeThin,
            MaterialTheme.colorScheme.outlineVariant
        )
    }

    val container = if (isCapital) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    OutlinedCard(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        border = border,
        shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
        colors = CardDefaults.outlinedCardColors(containerColor = container)
    ) {
        Column(
            modifier = Modifier.padding(
                start = LocalAppDimens.current.paddingMedium,
                end = LocalAppDimens.current.paddingMedium,
                bottom = LocalAppDimens.current.paddingMedium,
                top = LocalAppDimens.current.paddingSmall,
            ),
            verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingTiny)
                ) {
                    Text(
                        text = city.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isCapital) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Столица",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "№${city.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить",
                        tint = color
                    )
                }
            }

            MetricRow(
                "Население" to Labels.value(city.population),
                "Площадь" to Labels.value(city.area)
            )
            MetricRow(
                "Широта" to Labels.value(city.coordinatesX),
                "Долгота" to Labels.value(city.coordinatesY)
            )
            MetricRow(
                "Плотность" to Labels.value(city.populationDensity),
                "Высота" to Labels.value(city.metersAboveSeaLevel)
            )
            MetricRow(
                "Климат" to Climate.labelOf(city.climate),
                "Уровень жизни" to StandardOfLiving.labelOf(city.standardOfLiving)
            )
            MetricRow(
                "Губернатор" to (city.governorName?.let { "$it, ${city.governorAge} лет" } ?: Labels.EMPTY)
            )
            MetricRow(
                "Основан" to Labels.date(city.establishmentDate),
                "Создан" to Labels.date(city.creationDate)
            )
        }
    }
}
