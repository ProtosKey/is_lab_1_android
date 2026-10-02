package dev.protoskey.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.protoskey.theme.LocalAppDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Select(
    label: String,
    selected: T,
    options: List<Pair<T, String>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    placeholder: String = "Не выбрано"
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val current = options.find { it.first == selected }?.second ?: placeholder

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = current,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            supportingText = error?.let { { Text(it) } },
            isError = error != null,
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (error != null) colors.error.copy(alpha = 0.5f)
                else colors.outline.copy(alpha = 0.5f),
                focusedBorderColor = if (error != null) colors.error else colors.primary,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            )
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = MaterialTheme.shapes.large)
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = colors.surface,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(
                    LocalAppDimens.current.strokeThin,
                    colors.outlineVariant
                )
            ) {
                if (options.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Нет вариантов", color = colors.onSurfaceVariant) },
                        onClick = { expanded = false },
                        enabled = false
                    )
                }
                options.forEach { (value, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.onSurfaceVariant
                            )
                        },
                        onClick = {
                            onSelect(value)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = colors.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
