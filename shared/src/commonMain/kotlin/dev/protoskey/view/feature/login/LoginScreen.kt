package dev.protoskey.view.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import dev.protoskey.data.MainStore
import dev.protoskey.data.repository.AuthRepository
import dev.protoskey.presentation.viewmodel.LoginViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.Field
import dev.protoskey.view.component.FormButton
import dev.protoskey.view.component.Loading
import dev.protoskey.view.component.Message
import dev.protoskey.view.component.Section
import dev.protoskey.view.component.Title
import org.koin.compose.koinInject

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val store = koinInject<MainStore>()
        val auth = koinInject<AuthRepository>()
        val viewModel = viewModel<LoginViewModel>(factory = factory { LoginViewModel(store, auth) })
        val state by viewModel.loginState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()

        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = LocalAppDimens.current.paddingMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = LocalAppDimens.current.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingMedium)
                ) {
                    Title(label = "Вход")

                    Section(
                        title = "Сервер",
                        description = "Укажите адрес сервера системы"
                    ) {
                        Field(
                            value = state.server,
                            onValueChange = viewModel::updateServer,
                            label = "Адрес",
                            keyboardType = KeyboardType.Uri,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Section(
                        title = "Учётная запись",
                        description = "Войдите или зарегистрируйтесь, чтобы работать с городами"
                    ) {
                        Field(
                            value = state.login,
                            onValueChange = viewModel::updateLogin,
                            label = "Логин",
                            error = state.errors["login"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Field(
                            value = state.password,
                            onValueChange = viewModel::updatePassword,
                            label = "Пароль",
                            error = state.errors["password"],
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                            FormButton(
                                label = "Войти",
                                onClick = viewModel::signIn,
                                enabled = !state.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                            FormButton(
                                label = "Регистрация",
                                onClick = viewModel::signUp,
                                enabled = !state.isLoading,
                                isOutlined = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (state.isLoading) {
                    Loading(modifier = Modifier.align(Alignment.Center))
                }

                Message(
                    message = message.message,
                    isVisible = message.isVisible,
                    onClick = { viewModel.hideMessage() },
                    type = message.messageType
                )
            }
        }
    }
}
