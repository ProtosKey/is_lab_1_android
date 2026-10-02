package dev.protoskey.data.repository

import dev.protoskey.data.MainStore
import dev.protoskey.data.model.Account
import dev.protoskey.data.model.Credentials
import dev.protoskey.data.network.RestClient
import dev.protoskey.data.network.Transport

class AuthRepository(
    private val client: RestClient,
    private val transport: Transport,
    private val store: MainStore
) {
    suspend fun login(login: String, password: String) {
        val account = client.post<Credentials, Account>("/auth/login", Credentials(login, password))
        store.signIn(account.login)
    }

    suspend fun register(login: String, password: String) {
        val account = client.post<Credentials, Account>("/auth/register", Credentials(login, password))
        store.signIn(account.login)
    }

    suspend fun logout() {
        try {
            client.send("POST", "/auth/logout")
        } finally {
            transport.clearSession()
            store.signOut()
        }
    }
}
