package dev.protoskey.view.feature.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.protoskey.data.MainStore
import dev.protoskey.data.repository.CityRepository
import dev.protoskey.presentation.viewmodel.CitiesViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.ActionButton
import dev.protoskey.view.component.Empty
import dev.protoskey.view.component.Field
import dev.protoskey.view.component.Loading
import dev.protoskey.view.component.Message
import dev.protoskey.view.component.NavigationBar
import dev.protoskey.view.component.Select
import dev.protoskey.view.component.Title
import dev.protoskey.view.feature.cities.component.CityCard
import dev.protoskey.view.feature.cities.component.Pager
import dev.protoskey.view.feature.cities.component.Sorting
import dev.protoskey.view.feature.city.CityScreen
import org.koin.compose.koinInject

private val filterFields = listOf(
    "name" to "Название",
    "governor" to "Губернатор",
    "climate" to "Климат",
    "standardOfLiving" to "Уровень жизни"
)

class CitiesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val cities = koinInject<CityRepository>()
        val viewModel = viewModel<CitiesViewModel>(factory = factory { CitiesViewModel(store, cities) })
        val state by viewModel.citiesState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        var height by remember { mutableStateOf(0.dp) }

        Scaffold(
            bottomBar = { NavigationBar(navigator) },
            floatingActionButton = {
                ActionButton(
                    icon = Icons.Default.Add,
                    label = "Добавить",
                    description = "Добавить город",
                    onClick = { navigator.push(CityScreen(null)) },
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
                    Title(label = "Города")

                    Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                        Select(
                            label = "Искать по",
                            selected = state.query.filterField,
                            options = filterFields,
                            onSelect = viewModel::updateFilterField,
                            modifier = Modifier.weight(0.45f)
                        )
                        Field(
                            value = state.query.filterValue,
                            onValueChange = viewModel::updateFilterValue,
                            label = "Поиск",
                            modifier = Modifier.weight(0.55f)
                        )
                    }

                    Sorting(
                        field = state.query.sortField,
                        ascending = state.query.ascending,
                        onField = viewModel::updateSortField,
                        onDirection = viewModel::toggleSortDirection
                    )

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        if (state.cities.isEmpty() && state.isLoading) {
                            Loading()
                        } else if (state.cities.isEmpty()) {
                            Empty(
                                help = if (state.query.filterValue.isBlank()) "Городов пока нет. Нажмите «Добавить», чтобы создать первый"
                                else "Ничего не найдено",
                                icon = Icons.Default.LocationCity
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall),
                                contentPadding = PaddingValues(vertical = LocalAppDimens.current.paddingSmall)
                            ) {
                                items(items = state.cities, key = { it.id ?: 0 }) { city ->
                                    CityCard(
                                        city = city,
                                        onOpen = { city.id?.let { navigator.push(CityScreen(it)) } }
                                    )
                                }
                            }
                        }
                        if (state.isLoading && state.cities.isNotEmpty()) {
                            Loading(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    Pager(
                        page = state.query.page,
                        pageCount = state.pageCount,
                        totalCount = state.totalCount,
                        onPrevious = viewModel::previousPage,
                        onNext = viewModel::nextPage,
                        modifier = Modifier
                            .padding(bottom = LocalAppDimens.current.paddingMedium)
                            .height(maxOf(height, LocalAppDimens.current.message))
                    )
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
    }
}
