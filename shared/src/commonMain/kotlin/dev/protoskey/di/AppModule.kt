package dev.protoskey.di

import dev.protoskey.data.MainStore
import dev.protoskey.data.network.LiveUpdates
import dev.protoskey.data.network.RestClient
import dev.protoskey.data.repository.AuthRepository
import dev.protoskey.data.repository.CityRepository
import dev.protoskey.data.repository.ReferenceRepository
import dev.protoskey.data.repository.SpecialRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    single { MainStore() }
    single { RestClient(get(), get()) }
    single(createdAtStart = true) { LiveUpdates(get(), get()) }
    single { AuthRepository(get(), get(), get()) }
    single { CityRepository(get()) }
    single { ReferenceRepository(get()) }
    single { SpecialRepository(get()) }
}

expect val platformModule: Module

private val koinInit by lazy { startKoin { modules(platformModule, appModule) } }

fun initKoin() {
    koinInit
}
