package dev.protoskey.data.utils

import dev.protoskey.data.model.MessageType

object Defaults {
    fun exception(): String = "Неожиданная ошибка"

    fun message(): String = "Начните работу..."

    fun messageType(): MessageType = MessageType.GOOD

    fun visible(): Boolean = false

    // 10.0.2.2 — адрес компьютера-хоста из эмулятора Android
    fun server(): String = "http://10.0.2.2:8080/is_lab_1"

    fun pageSize(): Int = 10

    fun reconnectDelay(): Long = 3000L
}
