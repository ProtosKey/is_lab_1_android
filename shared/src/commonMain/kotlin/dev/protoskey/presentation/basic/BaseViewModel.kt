package dev.protoskey.presentation.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.MessageType
import dev.protoskey.data.network.ApiException
import dev.protoskey.data.utils.Defaults
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseViewModel(protected val store: MainStore) : ViewModel(), HaveMessage {
    private var messageJob: Job? = null

    override fun hideMessage() {
        messageJob?.cancel()
        store.hideMessage()
    }

    override fun showMessage(message: String, messageType: MessageType) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            if (store.notification.value.isVisible) {
                store.hideMessage()
                delay(250)
            }
            store.showMessage(message, messageType)
        }
    }

    protected fun request(
        onError: (ApiException) -> Unit = {},
        block: suspend () -> Unit
    ): Job = viewModelScope.launch {
        try {
            block()
        } catch (e: ApiException) {
            onError(e)
            showMessage(e.message ?: Defaults.exception(), MessageType.ERROR)
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageJob?.cancel()
    }
}
