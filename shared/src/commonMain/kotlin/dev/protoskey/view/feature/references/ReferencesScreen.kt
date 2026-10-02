package dev.protoskey.view.feature.references

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.protoskey.data.MainStore
import dev.protoskey.data.repository.ReferenceRepository
import dev.protoskey.presentation.model.ReferenceTab
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.presentation.viewmodel.ReferencesViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.ActionButton
import dev.protoskey.view.component.Empty
import dev.protoskey.view.component.FormButton
import dev.protoskey.view.component.Loading
import dev.protoskey.view.component.Message
import dev.protoskey.view.component.NavigationBar
import dev.protoskey.view.component.Title
import dev.protoskey.view.feature.references.component.DeletionDialog
import dev.protoskey.view.feature.references.component.EditorDialog
import dev.protoskey.view.feature.references.component.ReferenceCard
import org.koin.compose.koinInject

class ReferencesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val references = koinInject<ReferenceRepository>()
        val viewModel = viewModel<ReferencesViewModel>(factory = factory { ReferencesViewModel(store, references) })
        val state by viewModel.referencesState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        var height by remember { mutableStateOf(0.dp) }

        Scaffold(
            bottomBar = { NavigationBar(navigator) },
            floatingActionButton = {
                ActionButton(
                    icon = Icons.Default.Add,
                    label = "Добавить",
                    description = "Добавить объект",
                    onClick = viewModel::create,
                    onHeight = { height = it }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = LocalAppDimens.current.paddingMedium)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)
                ) {
                    Title(label = "Справочники")

                    Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                        ReferenceTab.entries.forEach { tab ->
                            FormButton(
                                label = tab.label,
                                onClick = { viewModel.selectTab(tab) },
                                isOutlined = state.tab != tab,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    val isEmpty = when (state.tab) {
                        ReferenceTab.COORDINATES -> state.coordinates.isEmpty()
                        ReferenceTab.HUMANS -> state.humans.isEmpty()
                    }
                    if (isEmpty && state.isLoading) {
                        Loading()
                    } else if (isEmpty) {
                        Empty(help = "Список пуст, нажмите добавить", icon = Icons.AutoMirrored.Filled.LibraryBooks)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall),
                            contentPadding = PaddingValues(
                                top = LocalAppDimens.current.paddingSmall,
                                bottom = height + LocalAppDimens.current.paddingLarge
                            )
                        ) {
                            when (state.tab) {
                                ReferenceTab.COORDINATES -> items(state.coordinates, key = { it.id ?: 0 }) { item ->
                                    ReferenceCard(
                                        title = "Координаты",
                                        id = item.id,
                                        metrics = listOf(
                                            "Широта" to Labels.value(item.x),
                                            "Долгота" to Labels.value(item.y)
                                        ),
                                        onEdit = { viewModel.edit(item) },
                                        onDelete = { viewModel.requestDelete(ReferenceTab.COORDINATES, item.id!!) }
                                    )
                                }

                                ReferenceTab.HUMANS -> items(state.humans, key = { it.id ?: 0 }) { item ->
                                    ReferenceCard(
                                        title = item.name.orEmpty(),
                                        id = item.id,
                                        metrics = listOf(
                                            "Возраст" to Labels.value(item.age),
                                            "Дата рождения" to Labels.date(item.birthday)
                                        ),
                                        onEdit = { viewModel.edit(item) },
                                        onDelete = { viewModel.requestDelete(ReferenceTab.HUMANS, item.id!!) }
                                    )
                                }
                            }
                        }
                    }
                }

                Message(
                    message = message.message,
                    isVisible = message.isVisible,
                    onClick = { viewModel.hideMessage() },
                    bottom = height,
                    type = message.messageType
                )
            }
        }

        state.editor?.let { editor ->
            EditorDialog(
                editor = editor,
                onChange = viewModel::updateEditor,
                onSave = viewModel::saveEditor,
                onDismiss = viewModel::closeEditor
            )
        }

        state.deletion?.let { deletion ->
            DeletionDialog(
                deletion = deletion,
                onReplacement = viewModel::updateReplacement,
                onConfirm = viewModel::confirmDelete,
                onDismiss = viewModel::closeDeletion
            )
        }
    }
}
