package dev.protoskey.view.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun Title(label: String) {
    val contentColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Text(
        text = label,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.fillMaxWidth(),
        color = contentColor
    )
}
