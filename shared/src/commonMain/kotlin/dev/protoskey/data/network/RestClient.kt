package dev.protoskey.data.network

import dev.protoskey.data.MainStore
import dev.protoskey.data.model.ErrorBody
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

class RestClient(
    private val transport: Transport,
    private val store: MainStore
) {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend inline fun <reified T> get(path: String): T =
        json.decodeFromString(send("GET", path))

    suspend inline fun <reified T> delete(path: String): T =
        json.decodeFromString(send("DELETE", path))

    suspend inline fun <reified B, reified T> post(path: String, body: B): T =
        json.decodeFromString(send("POST", path, json.encodeToString(body)))

    suspend inline fun <reified B, reified T> put(path: String, body: B): T =
        json.decodeFromString(send("PUT", path, json.encodeToString(body)))

    suspend fun send(method: String, path: String, body: String? = null): String {
        val response = try {
            transport.execute(HttpRequest(method, "${store.server.value}/api$path", body))
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            throw ApiException("Не удалось подключиться к серверу. Проверьте адрес и интернет")
        }

        if (response.code in 200..299) {
            return response.body
        }

        val error = runCatching { json.decodeFromString<ErrorBody>(response.body) }.getOrNull()
        if (response.code == 401 && !path.startsWith("/auth/")) {
            store.signOut()
        }
        throw ApiException(
            message = error?.message ?: "Сервер не смог обработать запрос, попробуйте ещё раз",
            fieldErrors = error?.fieldErrors.orEmpty(),
            code = response.code
        )
    }
}
