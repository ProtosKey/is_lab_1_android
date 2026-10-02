package dev.protoskey.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dev.protoskey.data.MainStore
import dev.protoskey.data.repository.AuthRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.state.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update

class AccountViewModel(
    store: MainStore,
    private val auth: AuthRepository
) : BaseViewModel(store) {
    private val _accountState = MutableStateFlow(AccountState())
    val accountState = _accountState.asStateFlow()

    init {
        combine(store.account, store.server) { account: String?, server: String ->
            _accountState.update { it.copy(login = account.orEmpty(), server = server) }
        }.launchIn(viewModelScope)
    }

    fun logout() = request { auth.logout() }
}
