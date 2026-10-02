package dev.protoskey.presentation.state

data class LoginState(
    val server: String = "",
    val login: String = "",
    val password: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)
