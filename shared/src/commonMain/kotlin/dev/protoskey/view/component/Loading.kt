package dev.protoskey.view.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.protoskey.theme.LocalAppDimens
import kotlinx.coroutines.delay

// Индикатор появляется с задержкой, чтобы не мигать на быстрых запросах
private const val SHOW_DELAY = 300L

@Composable
fun Loading(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(SHOW_DELAY)
        isVisible = true
    }

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(LocalAppDimens.current.paddingExtraLarge),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
