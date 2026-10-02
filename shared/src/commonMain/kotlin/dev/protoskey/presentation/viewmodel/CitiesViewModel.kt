package dev.protoskey.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.CityQuery
import dev.protoskey.data.repository.CityRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.state.CitiesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class CitiesViewModel(
    store: MainStore,
    private val cities: CityRepository
) : BaseViewModel(store) {
    private var loadJob: Job? = null
    private val _citiesState = MutableStateFlow(CitiesState())
    val citiesState = _citiesState.asStateFlow()
    val notification = store.notification

    init {
        store.changes.onEach { load() }.launchIn(viewModelScope)
    }

    fun updateFilterField(field: String) = changeQuery { it.copy(filterField = field, page = 1) }

    fun updateFilterValue(value: String) = changeQuery(debounce = true) { it.copy(filterValue = value, page = 1) }

    fun updateSortField(field: String) = changeQuery { it.copy(sortField = field) }

    fun toggleSortDirection() = changeQuery { it.copy(ascending = !it.ascending) }

    fun previousPage() {
        val page = _citiesState.value.query.page
        if (page > 1) changeQuery { it.copy(page = page - 1) }
    }

    fun nextPage() {
        val state = _citiesState.value
        if (state.query.page < state.pageCount) changeQuery { it.copy(page = state.query.page + 1) }
    }

    private fun changeQuery(debounce: Boolean = false, change: (CityQuery) -> CityQuery) {
        _citiesState.update { it.copy(query = change(it.query)) }
        load(if (debounce) FILTER_DELAY else 0L)
    }

    private fun load(delayMillis: Long = 0L) {
        loadJob?.cancel()
        loadJob = request {
            delay(delayMillis)
            _citiesState.update { it.copy(isLoading = true) }
            try {
                val query = _citiesState.value.query
                val page = cities.page(query)
                _citiesState.update { it.copy(cities = page.items, totalCount = page.totalCount) }

                // Если после удаления текущая страница опустела — переходим на последнюю
                val lastPage = _citiesState.value.pageCount
                if (page.items.isEmpty() && query.page > lastPage) {
                    changeQuery { it.copy(page = lastPage) }
                }
            } finally {
                _citiesState.update { it.copy(isLoading = false) }
            }
        }
    }

    private companion object {
        const val FILTER_DELAY = 300L
    }
}
