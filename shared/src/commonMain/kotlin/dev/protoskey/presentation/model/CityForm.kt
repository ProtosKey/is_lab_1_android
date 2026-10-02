package dev.protoskey.presentation.model

data class CityForm(
    val name: String = "",
    val coordinatesId: Int? = null,
    val governorId: Int? = null,
    val area: String = "",
    val population: String = "",
    val establishmentDate: String = "",
    val capital: Boolean = false,
    val metersAboveSeaLevel: String = "",
    val populationDensity: String = "",
    val climate: String? = null,
    val standardOfLiving: String? = null
)
