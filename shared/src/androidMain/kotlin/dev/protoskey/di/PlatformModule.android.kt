package dev.protoskey.di

import dev.protoskey.data.network.OkHttpTransport
import dev.protoskey.data.network.Transport
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Transport> { OkHttpTransport() }
}
