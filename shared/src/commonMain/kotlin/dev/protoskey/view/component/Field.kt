package dev.protoskey.view.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.protoskey.presentation.tools.DateMask
import dev.protoskey.theme.LocalAppDimens

@Composable
fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    mask: DateMask? = null
) {
    val colors = if (error != null) OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.error,
        unfocusedBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
    ) else OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    )

    OutlinedTextField(
        value = value,
        onValueChange = { text ->
            onValueChange(if (mask != null) text.filter(Char::isDigit).take(mask.length) else text)
        },
        label = { Text(label) },
        placeholder = (placeholder ?: mask?.placeholder)?.let { { Text(it) } },
        supportingText = error?.let { { Text(it) } },
        isError = error != null,
        shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                isPassword -> KeyboardType.Password
                mask != null -> KeyboardType.Number
                else -> keyboardType
            }
        ),
        visualTransformation = when {
            isPassword -> PasswordVisualTransformation()
            mask != null -> MaskTransformation(mask)
            else -> VisualTransformation.None
        },
        modifier = modifier,
        colors = colors
    )
}
