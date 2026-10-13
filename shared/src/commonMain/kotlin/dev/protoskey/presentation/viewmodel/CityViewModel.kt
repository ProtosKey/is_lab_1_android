package dev.protoskey.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.MessageType
import dev.protoskey.data.repository.CityRepository
import dev.protoskey.data.repository.ReferenceRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.mapper.CityFormMapper
import dev.protoskey.presentation.model.CityForm
import dev.protoskey.presentation.state.CityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class CityViewModel(
    store: MainStore,
    private val cities: CityRepository,
    private val references: ReferenceRepository,
    cityId: Int?
) : BaseViewModel(store) {
    private val _cityState = MutableStateFlow(CityState(id = cityId, isLoading = cityId != null))
    val cityState = _cityState.asStateFlow()
    val notification = store.notification

    init {
        store.changes.onEach { loadReferences() }.launchIn(viewModelScope)
        if (cityId != null) loadCity(cityId)
    }

    fun update(change: (CityForm) -> CityForm) = _cityState.update { it.copy(form = change(it.form)) }

    fun save() {
        val state = _cityState.value
        if (state.isLoading || state.isSaving) return

        val (city, localErrors) = CityFormMapper.toCity(state.form)
        if (localErrors.isNotEmpty()) {
            _cityState.update { it.copy(errors = localErrors) }
            showMessage("Исправьте поля, отмеченные красным", MessageType.ERROR)
            return
        }

        _cityState.update { it.copy(isSaving = true, errors = emptyMap()) }
        request(onError = { e -> _cityState.update { it.copy(errors = e.fieldErrors) } }) {
            try {
                if (state.id == null) cities.create(city) else cities.update(state.id, city)
                store.showMessage("Город «${city.name}» сохранён", MessageType.GOOD)
                _cityState.update { it.copy(isClosed = true) }
            } finally {
                _cityState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun delete() {
        val state = _cityState.value
        val id = state.id ?: return
        request {
            cities.delete(id)
            store.showMessage("Город «${state.form.name}» удалён", MessageType.GOOD)
            _cityState.update { it.copy(isClosed = true) }
        }
    }

    private fun loadCity(id: Int) {
        request(onError = { _cityState.update { it.copy(isClosed = true) } }) {
            try {
                val city = cities.byId(id)
                _cityState.update {
                    it.copy(form = CityFormMapper.toForm(city), creationDate = city.creationDate)
                }
            } finally {
                _cityState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadReferences() = request {
        val coordinates = references.coordinates()
        val humans = references.humans()
        _cityState.update { it.copy(coordinates = coordinates, humans = humans) }
    }
}
