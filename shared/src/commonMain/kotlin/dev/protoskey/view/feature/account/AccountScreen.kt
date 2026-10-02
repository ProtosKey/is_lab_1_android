package dev.protoskey.view.feature.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.protoskey.data.MainStore
import dev.protoskey.data.repository.AuthRepository
import dev.protoskey.presentation.viewmodel.AccountViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.FormButton
import dev.protoskey.view.component.NavigationBar
import dev.protoskey.view.component.Section
import dev.protoskey.view.component.Title
import org.koin.compose.koinInject

class AccountScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val auth = koinInject<AuthRepository>()
        val viewModel = viewModel<AccountViewModel>(factory = factory { AccountViewModel(store, auth) })
        val state by viewModel.accountState.collectAsStateWithLifecycle()

        Scaffold(
            bottomBar = { NavigationBar(navigator) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = LocalAppDimens.current.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingMedium)
            ) {
                Title(label = "Аккаунт")

                Section(title = state.login) {
                    Text(
                        text = "Сервер: ${state.server}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Изменения других пользователей появляются автоматически.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FormButton(
                        label = "Выйти",
                        onClick = viewModel::logout,
                        isDangerous = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
