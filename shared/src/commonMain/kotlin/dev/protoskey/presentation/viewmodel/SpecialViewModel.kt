package dev.protoskey.presentation.viewmodel

import dev.protoskey.data.MainStore
import dev.protoskey.data.model.Climate
import dev.protoskey.data.model.MessageType
import dev.protoskey.data.repository.SpecialRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.state.SpecialState
import dev.protoskey.presentation.tools.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpecialViewModel(
    store: MainStore,
    private val special: SpecialRepository
) : BaseViewModel(store) {
    private val _specialState = MutableStateFlow(SpecialState())
    val specialState = _specialState.asStateFlow()
    val notification = store.notification

    fun update(change: (SpecialState) -> SpecialState) = _specialState.update(change)

    fun deleteByClimate() {
        val climate = _specialState.value.climate
        if (climate == null) {
            fail(mapOf("climate" to "Выберите климат"))
            return
        }
        perform {
            val city = special.deleteOneByClimate(climate)
            showMessage(
                "Удалён город №${city.id} «${city.name}» (${Climate.labelOf(climate)})",
                MessageType.GOOD
            )
        }
    }

    fun countByMeters() {
        val parser = Parser.Result()
        val meters = parser.float("meters", _specialState.value.meters) ?: return fail(parser.errors)
        perform {
            val count = special.countByMeters(meters)
            _specialState.update { it.copy(count = count) }
        }
    }

    fun findByNamePart() {
        val part = _specialState.value.namePart.trim()
        if (part.isEmpty()) {
            fail(mapOf("namePart" to "Введите подстроку"))
            return
        }
        perform {
            val found = special.findByNamePart(part)
            _specialState.update { it.copy(found = found) }
        }
    }

    fun movePopulation() {
        val state = _specialState.value
        val parser = Parser.Result()
        val sourceId = parser.int("sourceId", state.sourceId)
        val targetId = parser.int("targetId", state.targetId)
        if (sourceId == null || targetId == null) return fail(parser.errors)
        perform {
            val target = special.movePopulation(sourceId, targetId)
            showMessage(
                "Население переселено в «${target.name}», теперь там ${target.population}",
                MessageType.GOOD
            )
        }
    }

    fun movePopulationToSmallest() {
        val parser = Parser.Result()
        val sourceId = parser.int("smallestSourceId", _specialState.value.smallestSourceId)
            ?: return fail(parser.errors)
        perform {
            val target = special.movePopulationToSmallest(sourceId)
            showMessage(
                "Население переселено в «${target.name}» (№${target.id}), теперь там ${target.population}",
                MessageType.GOOD
            )
        }
    }

    private fun fail(errors: Map<String, String>) {
        _specialState.update { it.copy(errors = errors) }
        showMessage(errors.values.first(), MessageType.ERROR)
    }

    private fun perform(block: suspend () -> Unit) {
        if (_specialState.value.isLoading) return
        _specialState.update { it.copy(errors = emptyMap(), isLoading = true) }
        request(onError = { e -> _specialState.update { it.copy(errors = e.fieldErrors) } }) {
            try {
                block()
            } finally {
                _specialState.update { it.copy(isLoading = false) }
            }
        }
    }
}
