package dev.protoskey.data.model

import dev.protoskey.data.utils.Defaults

data class Notification(
    val message: String = Defaults.message(),
    val messageType: MessageType = Defaults.messageType(),
    val isVisible: Boolean = Defaults.visible()
)
