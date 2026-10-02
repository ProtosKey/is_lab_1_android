package dev.protoskey.view.feature.references.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import dev.protoskey.presentation.model.Editor
import dev.protoskey.presentation.tools.DateMask
import dev.protoskey.view.component.Dialog
import dev.protoskey.view.component.Field

@Composable
fun EditorDialog(
    editor: Editor,
    onChange: (Editor) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val title = when (editor) {
        is Editor.CoordinatesEditor -> editor.id?.let { "Координаты №$it" } ?: "Новые координаты"
        is Editor.HumanEditor -> editor.id?.let { "Губернатор №$it" } ?: "Новый губернатор"
    }

    Dialog(
        title = title,
        confirmLabel = "Сохранить",
        onConfirm = onSave,
        onDismiss = onDismiss
    ) {
        when (editor) {
            is Editor.CoordinatesEditor -> {
                Field(
                    value = editor.x,
                    onValueChange = { onChange(editor.copy(x = it)) },
                    label = "Широта",
                    error = editor.errors["x"],
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
                Field(
                    value = editor.y,
                    onValueChange = { onChange(editor.copy(y = it)) },
                    label = "Долгота",
                    error = editor.errors["y"],
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is Editor.HumanEditor -> {
                Field(
                    value = editor.name,
                    onValueChange = { onChange(editor.copy(name = it)) },
                    label = "Имя",
                    error = editor.errors["name"],
                    modifier = Modifier.fillMaxWidth()
                )
                Field(
                    value = editor.age,
                    onValueChange = { onChange(editor.copy(age = it)) },
                    label = "Возраст",
                    error = editor.errors["age"],
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
                Field(
                    value = editor.birthday,
                    onValueChange = { onChange(editor.copy(birthday = it)) },
                    label = "Дата рождения",
                    mask = DateMask.DATE,
                    error = editor.errors["birthday"],
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
