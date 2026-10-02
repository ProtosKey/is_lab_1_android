package dev.protoskey.view.feature.references.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.component.MetricRow

@Composable
fun ReferenceCard(
    title: String,
    id: Int?,
    metrics: List<Pair<String, String>>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            LocalAppDimens.current.strokeThin,
            MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(
                start = LocalAppDimens.current.paddingMedium,
                end = LocalAppDimens.current.paddingSmall,
                bottom = LocalAppDimens.current.paddingMedium,
                top = LocalAppDimens.current.paddingSmall,
            ),
            verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "№$id",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(modifier = Modifier.padding(end = LocalAppDimens.current.paddingSmall)) {
                MetricRow(*metrics.toTypedArray())
            }
        }
    }
}
