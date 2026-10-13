package dev.protoskey.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import dev.protoskey.data.model.MessageType
import dev.protoskey.data.repository.ReferenceRepository
import dev.protoskey.presentation.basic.BaseViewModel
import dev.protoskey.presentation.model.Deletion
import dev.protoskey.presentation.model.Editor
import dev.protoskey.presentation.model.ReferenceTab
import dev.protoskey.presentation.state.ReferencesState
import dev.protoskey.presentation.tools.DateMask
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.presentation.tools.Parser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ReferencesViewModel(
    store: MainStore,
    private val references: ReferenceRepository
) : BaseViewModel(store) {
    private val _referencesState = MutableStateFlow(ReferencesState())
    val referencesState = _referencesState.asStateFlow()
    val notification = store.notification

    init {
        store.changes.onEach { load() }.launchIn(viewModelScope)
    }

    fun selectTab(tab: ReferenceTab) = _referencesState.update { it.copy(tab = tab) }

    fun create() = _referencesState.update {
        it.copy(
            editor = when (it.tab) {
                ReferenceTab.COORDINATES -> Editor.CoordinatesEditor()
                ReferenceTab.HUMANS -> Editor.HumanEditor()
            }
        )
    }

    fun edit(coordinates: Coordinates) = _referencesState.update {
        it.copy(editor = Editor.CoordinatesEditor(coordinates.id, "${coordinates.x}", "${coordinates.y}"))
    }

    fun edit(human: Human) = _referencesState.update {
        it.copy(editor = Editor.HumanEditor(human.id, human.name.orEmpty(), "${human.age}", DateMask.DATE.digits(human.birthday)))
    }

    fun updateEditor(editor: Editor) = _referencesState.update { it.copy(editor = editor) }

    fun closeEditor() = _referencesState.update { it.copy(editor = null) }

    fun saveEditor() {
        when (val editor = _referencesState.value.editor) {
            is Editor.CoordinatesEditor -> saveCoordinates(editor)
            is Editor.HumanEditor -> saveHuman(editor)
            null -> Unit
        }
    }

    fun requestDelete(tab: ReferenceTab, id: Int) = request {
        val state = _referencesState.value
        val deletion = when (tab) {
            ReferenceTab.COORDINATES -> Deletion(
                tab = tab,
                id = id,
                title = state.coordinates.find { it.id == id }?.let { "широта ${it.x}, долгота ${it.y}" } ?: "№$id",
                linkedCities = references.citiesWithCoordinates(id).size,
                candidates = state.coordinates.filter { it.id != id }.map { it.id!! to Labels.coordinates(it) }
            )

            ReferenceTab.HUMANS -> Deletion(
                tab = tab,
                id = id,
                title = state.humans.find { it.id == id }?.name ?: "№$id",
                linkedCities = references.citiesWithGovernor(id).size,
                candidates = state.humans.filter { it.id != id }.map { it.id!! to Labels.human(it) }
            )
        }
        _referencesState.update { it.copy(deletion = deletion) }
    }

    fun updateReplacement(id: Int) = _referencesState.update {
        it.copy(deletion = it.deletion?.copy(replacementId = id))
    }

    fun closeDeletion() = _referencesState.update { it.copy(deletion = null) }

    fun confirmDelete() {
        val deletion = _referencesState.value.deletion ?: return
        if (!deletion.canDelete) return
        request {
            when (deletion.tab) {
                ReferenceTab.COORDINATES -> references.deleteCoordinates(deletion.id, deletion.replacementId)
                ReferenceTab.HUMANS -> references.deleteHuman(deletion.id, deletion.replacementId)
            }
            _referencesState.update { it.copy(deletion = null) }
            showMessage(
                when (deletion.tab) {
                    ReferenceTab.COORDINATES -> "Координаты удалены"
                    ReferenceTab.HUMANS -> "Губернатор «${deletion.title}» удалён"
                },
                MessageType.GOOD
            )
        }
    }

    private fun saveCoordinates(editor: Editor.CoordinatesEditor) {
        val parser = Parser.Result()
        val coordinates = Coordinates(x = parser.int("x", editor.x), y = parser.int("y", editor.y))
        if (coordinates.x != null && coordinates.x > MAX_X) {
            parser.errors["x"] = "Широта не может быть больше $MAX_X"
        }
        submit(editor, parser.errors) {
            if (editor.id == null) references.createCoordinates(coordinates)
            else references.updateCoordinates(editor.id, coordinates)
        }
    }

    private fun saveHuman(editor: Editor.HumanEditor) {
        val parser = Parser.Result()
        val human = Human(
            name = editor.name.trim(),
            age = parser.long("age", editor.age),
            birthday = parser.date("birthday", editor.birthday, DateMask.DATE)
        )
        submit(editor, parser.errors) {
            if (editor.id == null) references.createHuman(human)
            else references.updateHuman(editor.id, human)
        }
    }

    private fun submit(editor: Editor, localErrors: Map<String, String>, save: suspend () -> Unit) {
        if (localErrors.isNotEmpty()) {
            updateErrors(editor, localErrors)
            return
        }
        request(onError = { e -> updateErrors(editor, e.fieldErrors) }) {
            save()
            _referencesState.update { it.copy(editor = null) }
            showMessage(
                when (editor) {
                    is Editor.CoordinatesEditor -> "Координаты сохранены"
                    is Editor.HumanEditor -> "Губернатор «${editor.name.trim()}» сохранён"
                },
                MessageType.GOOD
            )
        }
    }

    private fun updateErrors(editor: Editor, errors: Map<String, String>) = _referencesState.update {
        it.copy(
            editor = when (editor) {
                is Editor.CoordinatesEditor -> editor.copy(errors = errors)
                is Editor.HumanEditor -> editor.copy(errors = errors)
            }
        )
    }

    private fun load() = request(onError = { _referencesState.update { it.copy(isLoading = false) } }) {
        val coordinates = references.coordinates()
        val humans = references.humans()
        _referencesState.update { it.copy(coordinates = coordinates, humans = humans, isLoading = false) }
    }

    private companion object {
        const val MAX_X = 974
    }
}
