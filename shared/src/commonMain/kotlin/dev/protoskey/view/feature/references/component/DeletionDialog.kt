package dev.protoskey.view.feature.references.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.protoskey.presentation.model.Deletion
import dev.protoskey.presentation.model.ReferenceTab
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.view.component.Dialog
import dev.protoskey.view.component.Select

@Composable
fun DeletionDialog(
    deletion: Deletion,
    onReplacement: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        title = when (deletion.tab) {
            ReferenceTab.COORDINATES -> "Удалить координаты?"
            ReferenceTab.HUMANS -> "Удалить губернатора?"
        },
        confirmLabel = "Удалить",
        isDangerous = true,
        confirmEnabled = deletion.canDelete,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        val cities = deletion.linkedCities
        Text(
            when (deletion.tab) {
                ReferenceTab.COORDINATES -> "Координаты (${deletion.title}) будут удалены"
                ReferenceTab.HUMANS -> "Губернатор «${deletion.title}» будет удалён"
            }
        )
        if (cities > 0) {
            Text(
                when (deletion.tab) {
                    ReferenceTab.COORDINATES ->
                        "Они указаны у $cities ${Labels.plural(cities, "города", "городов", "городов")}"

                    ReferenceTab.HUMANS ->
                        "Он управляет $cities ${Labels.plural(cities, "городом", "городами", "городами")}"
                }
            )
            Select(
                label = when (deletion.tab) {
                    ReferenceTab.COORDINATES -> "Новые координаты"
                    ReferenceTab.HUMANS -> "Новый губернатор"
                },
                selected = deletion.replacementId,
                options = deletion.candidates.map { (id, label) -> id as Int? to label },
                onSelect = { id -> id?.let(onReplacement) },
                error = if (deletion.candidates.isEmpty()) "Необходимо создать замену" else null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
