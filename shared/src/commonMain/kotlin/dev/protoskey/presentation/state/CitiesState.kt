package dev.protoskey.presentation.state

import dev.protoskey.data.model.City
import dev.protoskey.data.model.CityQuery

data class CitiesState(
    val cities: List<City> = emptyList(),
    val totalCount: Long = 0,
    val query: CityQuery = CityQuery(),
    val isLoading: Boolean = true
) {
    val pageCount: Int
        get() = maxOf(1, ((totalCount + query.pageSize - 1) / query.pageSize).toInt())
}
