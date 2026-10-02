package dev.protoskey.data.network

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpTransport : Transport {
    private val cookies = MemoryCookieJar()
    private val client = OkHttpClient.Builder()
        .cookieJar(cookies)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    override suspend fun execute(request: HttpRequest): HttpResponse {
        val body = when {
            request.body != null -> request.body.toRequestBody(JSON)
            request.method == "POST" || request.method == "PUT" -> "".toRequestBody(JSON)
            else -> null
        }
        val call = client.newCall(
            Request.Builder()
                .url(request.url)
                .method(request.method, body)
                .build()
        )

        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.use { HttpResponse(it.code, it.body?.string().orEmpty()) }
                    continuation.resume(result)
                }
            })
        }
    }

    override fun listen(url: String, listener: SocketListener): Socket {
        val socket = client.newWebSocket(
            Request.Builder().url(url).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) = listener.onOpen()

                override fun onMessage(webSocket: WebSocket, text: String) = listener.onMessage(text)

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = listener.onClosed()

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) =
                    listener.onClosed()
            }
        )
        return Socket { socket.close(NORMAL_CLOSURE, null) }
    }

    override fun clearSession() {
        cookies.clear()
    }

    private companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
        const val NORMAL_CLOSURE = 1000
    }
}
