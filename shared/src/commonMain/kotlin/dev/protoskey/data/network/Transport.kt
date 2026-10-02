package dev.protoskey.data.network

data class HttpRequest(val method: String, val url: String, val body: String? = null)

data class HttpResponse(val code: Int, val body: String)

interface SocketListener {
    fun onOpen()
    fun onMessage(text: String)
    fun onClosed()
}

fun interface Socket {
    fun close()
}

/**
 * Платформенный транспорт: HTTP-запросы и WebSocket с общей сессией (cookie).
 * На Android реализован через OkHttp.
 */
interface Transport {
    suspend fun execute(request: HttpRequest): HttpResponse
    fun listen(url: String, listener: SocketListener): Socket
    fun clearSession()
}
