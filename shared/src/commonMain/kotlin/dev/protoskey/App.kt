package dev.protoskey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import dev.protoskey.data.MainStore
import dev.protoskey.theme.AppTheme
import dev.protoskey.view.feature.cities.CitiesScreen
import dev.protoskey.view.feature.login.LoginScreen
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Preview
@Composable
fun App(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}
) = AppTheme(onThemeChanged) {
    KoinContext {
        val store = koinInject<MainStore>()
        val account by store.account.collectAsStateWithLifecycle()
        val isSignedIn = account != null

        key(isSignedIn) {
            Navigator(if (isSignedIn) CitiesScreen() else LoginScreen())
        }
    }
}
