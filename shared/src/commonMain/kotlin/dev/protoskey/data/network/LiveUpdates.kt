package dev.protoskey.data.network

import dev.protoskey.data.MainStore
import dev.protoskey.data.utils.Defaults
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Держит WebSocket к /ws/cities, пока пользователь вошёл в систему,
 * и переподключается при обрыве.
 */
class LiveUpdates(
    private val transport: Transport,
    private val store: MainStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    init {
        store.account.onEach { account ->
            if (account != null) start() else stop()
        }.launchIn(scope)
    }

    private fun start() {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                val closed = CompletableDeferred<Unit>()
                val socket = transport.listen(socketUrl(), object : SocketListener {
                    override fun onOpen() = store.notifyChanged()
                    override fun onMessage(text: String) = store.notifyChanged()
                    override fun onClosed() {
                        closed.complete(Unit)
                    }
                })
                try {
                    closed.await()
                } finally {
                    socket.close()
                }
                delay(Defaults.reconnectDelay())
            }
        }
    }

    private fun stop() {
        job?.cancel()
        job = null
    }

    private fun socketUrl(): String =
        store.server.value.replaceFirst("http", "ws") + "/ws/cities"
}
