package dev.protoskey.view.feature.city

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.protoskey.data.MainStore
import dev.protoskey.data.model.Climate
import dev.protoskey.data.model.StandardOfLiving
import dev.protoskey.data.repository.CityRepository
import dev.protoskey.data.repository.ReferenceRepository
import dev.protoskey.presentation.tools.DateMask
import dev.protoskey.presentation.tools.Labels
import dev.protoskey.presentation.viewmodel.CityViewModel
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.basic.factory
import dev.protoskey.view.component.ActionButton
import dev.protoskey.view.component.Dialog
import dev.protoskey.view.component.Field
import dev.protoskey.view.component.Loading
import dev.protoskey.view.component.Message
import dev.protoskey.view.component.NavigationBar
import dev.protoskey.view.component.Select
import dev.protoskey.view.component.TitleBar
import dev.protoskey.view.feature.city.component.Related
import org.koin.compose.koinInject

class CityScreen(private val cityId: Int?) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val store = koinInject<MainStore>()
        val cities = koinInject<CityRepository>()
        val references = koinInject<ReferenceRepository>()
        val viewModel = viewModel<CityViewModel>(
            key = key,
            factory = factory { CityViewModel(store, cities, references, cityId) }
        )
        val state by viewModel.cityState.collectAsStateWithLifecycle()
        val message by viewModel.notification.collectAsStateWithLifecycle()
        var height by remember { mutableStateOf(0.dp) }
        var isDeleting by remember { mutableStateOf(false) }
        val form = state.form
        val errors = state.errors

        LaunchedEffect(state.isClosed) {
            if (state.isClosed) navigator.pop()
        }

        Scaffold(
            bottomBar = { NavigationBar(navigator) },
            floatingActionButton = {
                ActionButton(
                    icon = Icons.Default.Save,
                    label = "Сохранить",
                    description = "Сохранить город",
                    onClick = viewModel::save,
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
                Column(modifier = Modifier.fillMaxSize()) {
                    TitleBar(
                        label = if (state.id == null) "Новый город" else "Город №${state.id}",
                        onBack = { navigator.pop() }
                    ) {
                        if (state.id != null) {
                            IconButton(onClick = { isDeleting = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить город",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    if (state.isLoading) {
                        Loading()
                    } else Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingTiny)
                    ) {
                        Field(
                            value = form.name,
                            onValueChange = { value -> viewModel.update { it.copy(name = value) } },
                            label = "Название",
                            error = errors["name"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Select(
                            label = "Координаты",
                            selected = form.coordinatesId,
                            options = state.coordinates.map { it.id to Labels.coordinates(it) },
                            onSelect = { id -> viewModel.update { it.copy(coordinatesId = id) } },
                            error = errors["coordinatesId"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Select(
                            label = "Губернатор",
                            selected = form.governorId,
                            options = state.humans.map { it.id to Labels.human(it) },
                            onSelect = { id -> viewModel.update { it.copy(governorId = id) } },
                            error = errors["governorId"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                            Field(
                                value = form.area,
                                onValueChange = { value -> viewModel.update { it.copy(area = value) } },
                                label = "Площадь",
                                error = errors["area"],
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            Field(
                                value = form.population,
                                onValueChange = { value -> viewModel.update { it.copy(population = value) } },
                                label = "Население",
                                error = errors["population"],
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(LocalAppDimens.current.paddingSmall)) {
                            Field(
                                value = form.populationDensity,
                                onValueChange = { value -> viewModel.update { it.copy(populationDensity = value) } },
                                label = "Плотность",
                                error = errors["populationDensity"],
                                keyboardType = KeyboardType.Decimal,
                                modifier = Modifier.weight(1f)
                            )
                            Field(
                                value = form.metersAboveSeaLevel,
                                onValueChange = { value -> viewModel.update { it.copy(metersAboveSeaLevel = value) } },
                                label = "Высота",
                                error = errors["metersAboveSeaLevel"],
                                keyboardType = KeyboardType.Decimal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Field(
                            value = form.establishmentDate,
                            onValueChange = { value -> viewModel.update { it.copy(establishmentDate = value) } },
                            label = "Дата основания",
                            mask = DateMask.DATE_TIME,
                            error = errors["establishmentDate"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Select(
                            label = "Климат",
                            selected = form.climate,
                            options = listOf(null to "Не указан") +
                                Climate.entries.map { it.name to it.label },
                            onSelect = { climate -> viewModel.update { it.copy(climate = climate) } },
                            error = errors["climate"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Select(
                            label = "Уровень жизни",
                            selected = form.standardOfLiving,
                            options = StandardOfLiving.entries.map { it.name to it.label },
                            onSelect = { standard -> viewModel.update { it.copy(standardOfLiving = standard) } },
                            error = errors["standardOfLiving"],
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Столица",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = form.capital,
                                onCheckedChange = { checked -> viewModel.update { it.copy(capital = checked) } }
                            )
                        }

                        Related(
                            creationDate = state.creationDate,
                            coordinates = state.coordinates.find { it.id == form.coordinatesId },
                            governor = state.humans.find { it.id == form.governorId }
                        )
                        Spacer(Modifier.height(height + LocalAppDimens.current.paddingLarge))
                    }
                }

                if (state.isSaving) {
                    Loading(modifier = Modifier.align(Alignment.Center))
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

        if (isDeleting) {
            Dialog(
                title = "Удалить город?",
                confirmLabel = "Удалить",
                isDangerous = true,
                onConfirm = {
                    isDeleting = false
                    viewModel.delete()
                },
                onDismiss = { isDeleting = false }
            ) {
                Text("Город «${form.name}» будет удалён без возможности восстановления.")
            }
        }
    }
}
