package dev.protoskey.view.feature.cities.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.component.Select

private val sortFields = listOf(
    "id" to "Номер",
    "name" to "Название",
    "population" to "Население",
    "area" to "Площадь",
    "coordinatesX" to "Широта",
    "coordinatesY" to "Долгота",
    "populationDensity" to "Плотность",
    "metersAboveSeaLevel" to "Высота",
    "climate" to "Климат",
    "standardOfLiving" to "Уровень жизни",
    "governor" to "Губернатор",
    "capital" to "Столица",
    "establishmentDate" to "Дата основания",
    "creationDate" to "Дата создания"
)

@Composable
fun Sorting(
    field: String,
    ascending: Boolean,
    onField: (String) -> Unit,
    onDirection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)
    ) {
        Select(
            label = "Сортировка",
            selected = field,
            options = sortFields,
            onSelect = onField,
            modifier = Modifier.weight(1f)
        )
        FilledIconButton(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = LocalAppDimens.current.paddingSmall)
                .aspectRatio(1f),
            shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
            onClick = onDirection,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = if (ascending) "По возрастанию" else "По убыванию"
            )
        }
    }
}
