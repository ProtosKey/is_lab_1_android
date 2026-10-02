package dev.protoskey.data.model

data class CityQuery(
    val filterField: String = "name",
    val filterValue: String = "",
    val sortField: String = "id",
    val ascending: Boolean = true,
    val page: Int = 1,
    val pageSize: Int = 10
)
