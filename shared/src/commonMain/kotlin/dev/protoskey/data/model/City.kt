package dev.protoskey.data.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int? = null,
    val name: String? = null,
    val coordinatesId: Int? = null,
    val coordinatesX: Int? = null,
    val coordinatesY: Int? = null,
    val creationDate: String? = null,
    val area: Int? = null,
    val population: Int? = null,
    val establishmentDate: String? = null,
    val capital: Boolean? = null,
    val metersAboveSeaLevel: Float? = null,
    val populationDensity: Float? = null,
    val climate: String? = null,
    val standardOfLiving: String? = null,
    val governorId: Int? = null,
    val governorName: String? = null,
    val governorAge: Long? = null
)
