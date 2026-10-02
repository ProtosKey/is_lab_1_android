package dev.protoskey.presentation.viewmodel

import dev.protoskey.data.MainStore
import dev.protoskey.data.model.MessageType
import dev.protoskey.data.repository.AuthRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    store: MainStore,
    private val auth: AuthRepository
) : BaseViewModel(store) {
    private val _loginState = MutableStateFlow(LoginState(server = store.server.value))
    val loginState = _loginState.asStateFlow()
    val notification = store.notification

    fun updateServer(server: String) = _loginState.update { it.copy(server = server) }

    fun updateLogin(login: String) =
        _loginState.update { it.copy(login = login, errors = it.errors - "login") }

    fun updatePassword(password: String) =
        _loginState.update { it.copy(password = password, errors = it.errors - "password") }

    fun signIn() = submit { login, password -> auth.login(login, password) }

    fun signUp() = submit { login, password -> auth.register(login, password) }

    private fun submit(action: suspend (String, String) -> Unit) {
        val state = _loginState.value
        if (state.isLoading) return
        if (state.server.isBlank()) {
            showMessage("Укажите адрес сервера", MessageType.ERROR)
            return
        }
        store.updateServer(state.server)
        _loginState.update { it.copy(isLoading = true, errors = emptyMap()) }
        request(onError = { e -> _loginState.update { it.copy(errors = e.fieldErrors) } }) {
            try {
                action(state.login.trim(), state.password)
            } finally {
                _loginState.update { it.copy(isLoading = false) }
            }
        }
    }
}
