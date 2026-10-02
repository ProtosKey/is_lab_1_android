package dev.protoskey.presentation.basic

import dev.protoskey.data.model.MessageType

interface HaveMessage {
    fun hideMessage()
    fun showMessage(message: String, messageType: MessageType)
}
