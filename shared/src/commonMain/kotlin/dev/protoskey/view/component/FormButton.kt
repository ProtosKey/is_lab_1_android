package dev.protoskey.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.protoskey.theme.LocalAppDimens
import androidx.compose.material3.Button as MaterialButton

@Composable
fun FormButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDangerous: Boolean = false,
    isOutlined: Boolean = false,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(LocalAppDimens.current.radiusMedium)
    val colors = MaterialTheme.colorScheme

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            border = BorderStroke(LocalAppDimens.current.strokeThin, colors.outline.copy(alpha = 0.5f))
        ) { Text(label) }
    } else {
        MaterialButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDangerous) colors.error else colors.primary,
                contentColor = if (isDangerous) colors.onError else colors.onPrimary
            )
        ) { Text(label) }
    }
}
