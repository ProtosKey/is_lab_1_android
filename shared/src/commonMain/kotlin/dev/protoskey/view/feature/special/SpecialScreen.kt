package dev.protoskey.view.feature.special

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.Climate
import dev.protoskey.data.repository.SpecialRepository
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.presentation.viewmodel.SpecialViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.Field
import dev.protoskey.view.component.Loading
import dev.protoskey.view.component.FormButton
import dev.protoskey.view.component.Message
import dev.protoskey.view.component.NavigationBar
import dev.protoskey.view.component.Section
import dev.protoskey.view.component.Select
import dev.protoskey.view.component.Title
import dev.protoskey.view.feature.city.CityScreen
import org.koin.compose.koinInject

class SpecialScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val special = koinInject<SpecialRepository>()
        val viewModel = viewModel<SpecialViewModel>(factory = factory { SpecialViewModel(store, special) })
        val state by viewModel.specialState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        val errors = state.errors

        Scaffold(
            bottomBar = { NavigationBar(navigator) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = LocalAppDimens.current.paddingMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = LocalAppDimens.current.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingMedium)
                ) {
                    Title(label = "Операции")

                    Section(title = "Удаление по климату", description = "Удалит один город с выбранным климатом") {
                        Select(
                            label = "Климат",
                            selected = state.climate,
                            options = Climate.entries.map { it.name to it.label },
                            onSelect = { climate -> viewModel.update { it.copy(climate = climate) } },
                            error = errors["climate"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormButton(
                            label = "Удалить город",
                            onClick = viewModel::deleteByClimate,
                            isDangerous = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Section(title = "Подсчёт по высоте", description = "Сколько городов находится на указанной высоте над уровнем моря") {
                        Field(
                            value = state.meters,
                            onValueChange = { value -> viewModel.update { it.copy(meters = value) } },
                            label = "Высота",
                            error = errors["meters"] ?: errors["metersAboveSeaLevel"],
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormButton(label = "Посчитать", onClick = viewModel::countByMeters, modifier = Modifier.fillMaxWidth())
                        state.count?.let { Result("Найдено городов: $it") }
                    }

                    Section(title = "Поиск по названию", description = "Найдёт города, в названии которых есть введённый текст") {
                        Field(
                            value = state.namePart,
                            onValueChange = { value -> viewModel.update { it.copy(namePart = value) } },
                            label = "Часть названия",
                            error = errors["namePart"] ?: errors["part"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormButton(label = "Найти", onClick = viewModel::findByNamePart, modifier = Modifier.fillMaxWidth())
                        state.found?.let { found ->
                            if (found.isEmpty()) Result("Ничего не найдено")
                            found.forEach { city ->
                                Text(
                                    text = Labels.city(city),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = city.id != null) { navigator.push(CityScreen(city.id)) }
                                        .padding(vertical = LocalAppDimens.current.paddingTiny)
                                )
                            }
                        }
                    }

                    Section(title = "Переселение между городами", description = "Все жители переедут в город-получатель, а город-источник будет удалён") {
                        Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                            Field(
                                value = state.sourceId,
                                onValueChange = { value -> viewModel.update { it.copy(sourceId = value) } },
                                label = "Источник",
                                error = errors["sourceId"],
                                placeholder = "№ города",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            Field(
                                value = state.targetId,
                                onValueChange = { value -> viewModel.update { it.copy(targetId = value) } },
                                label = "Получатель",
                                error = errors["targetId"],
                                placeholder = "№ города",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        FormButton(label = "Переселить", onClick = viewModel::movePopulation, modifier = Modifier.fillMaxWidth())
                    }

                    Section(title = "Переселение в самый маленький город", description = "Все жители переедут в город с наименьшим населением, а город-источник будет удалён") {
                        Field(
                            value = state.smallestSourceId,
                            onValueChange = { value -> viewModel.update { it.copy(smallestSourceId = value) } },
                            label = "Источник",
                            error = errors["smallestSourceId"],
                            placeholder = "№ города",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormButton(
                            label = "Переселить",
                            onClick = viewModel::movePopulationToSmallest,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }

                if (state.isLoading) {
                    Loading(modifier = Modifier.align(Alignment.Center))
                }

                Message(
                    message = message.message,
                    isVisible = message.isVisible,
                    onClick = { viewModel.hideMessage() },
                    type = message.messageType
                )
            }
        }
    }
}

@Composable
private fun Result(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
