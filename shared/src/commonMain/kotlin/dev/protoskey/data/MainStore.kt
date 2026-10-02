package dev.protoskey.data

import dev.protoskey.data.model.MessageType
import dev.protoskey.data.model.Notification
import dev.protoskey.data.utils.Defaults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainStore {
    private val _server = MutableStateFlow<String>(Defaults.server())
    val server: StateFlow<String> = _server.asStateFlow()

    private val _account = MutableStateFlow<String?>(null)
    val account = _account.asStateFlow()

    // Увеличивается при каждом изменении данных на сервере (сигнал по WebSocket)
    private val _changes = MutableStateFlow(0)
    val changes = _changes.asStateFlow()

    private val _notification = MutableStateFlow(Notification())
    val notification = _notification.asStateFlow()

    fun updateServer(server: String) {
        _server.update { server.trim().trimEnd('/') }
    }

    fun signIn(login: String) {
        _account.update { login }
    }

    fun signOut() {
        _account.update { null }
    }

    fun notifyChanged() {
        _changes.update { it + 1 }
    }

    fun showMessage(message: String, messageType: MessageType) {
        _notification.update {
            it.copy(message = message, messageType = messageType, isVisible = true)
        }
    }

    fun hideMessage() {
        _notification.update {
            it.copy(isVisible = false)
        }
    }
}
