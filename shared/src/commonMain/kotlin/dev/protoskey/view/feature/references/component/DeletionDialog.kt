package dev.protoskey.view.feature.references.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.protoskey.presentation.model.Deletion
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
        title = "Удалить объект?",
        confirmLabel = "Удалить",
        isDangerous = true,
        confirmEnabled = deletion.canDelete,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text("Будет удалён объект ${deletion.title}")
        if (deletion.linkedCities > 0) {
            Text(
                "С ним связано городов: ${deletion.linkedCities}"
            )
            Select(
                label = "Замена",
                selected = deletion.replacementId,
                options = deletion.candidates.map { (id, label) -> id as Int? to label },
                onSelect = { id -> id?.let(onReplacement) },
                error = if (deletion.candidates.isEmpty()) "Необходимо создать замену" else null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
