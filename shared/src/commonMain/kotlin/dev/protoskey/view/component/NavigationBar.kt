package dev.protoskey.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import dev.protoskey.theme.LocalAppDimens
import dev.protoskey.view.feature.account.AccountScreen
import dev.protoskey.view.feature.cities.CitiesScreen
import dev.protoskey.view.feature.city.CityScreen
import dev.protoskey.view.feature.references.ReferencesScreen
import dev.protoskey.view.feature.special.SpecialScreen

@Composable
fun NavigationBar(navigator: Navigator) {
    val screen = navigator.lastItem

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .height(LocalAppDimens.current.navbar)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalAppDimens.current.strokeThin)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationElem(
                selected = screen is CitiesScreen || screen is CityScreen,
                onClick = { if (screen !is CitiesScreen) navigator.replaceAll(CitiesScreen()) },
                icon = Icons.Default.LocationCity,
                label = "Города"
            )

            NavigationElem(
                selected = screen is ReferencesScreen,
                onClick = { if (screen !is ReferencesScreen) navigator.replaceAll(ReferencesScreen()) },
                icon = Icons.AutoMirrored.Filled.LibraryBooks,
                label = "Справочники"
            )

            NavigationElem(
                selected = screen is SpecialScreen,
                onClick = { if (screen !is SpecialScreen) navigator.replaceAll(SpecialScreen()) },
                icon = Icons.Default.Bolt,
                label = "Операции"
            )

            NavigationElem(
                selected = screen is AccountScreen,
                onClick = { if (screen !is AccountScreen) navigator.replaceAll(AccountScreen()) },
                icon = Icons.Default.AccountCircle,
                label = "Аккаунт"
            )
        }
    }
}
