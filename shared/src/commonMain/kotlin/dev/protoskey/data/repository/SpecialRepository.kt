package dev.protoskey.data.repository

import dev.protoskey.data.model.City
import dev.protoskey.data.model.Count
import dev.protoskey.data.network.RestClient
import dev.protoskey.data.utils.UrlEncoder

class SpecialRepository(private val client: RestClient) {
    suspend fun deleteOneByClimate(climate: String): City =
        client.delete("/special/by-climate" + UrlEncoder.query("climate" to climate))

    suspend fun countByMeters(meters: Float): Long =
        client.get<Count>("/special/count-by-meters" + UrlEncoder.query("metersAboveSeaLevel" to meters)).count

    suspend fun findByNamePart(part: String): List<City> =
        client.get("/special/by-name-part" + UrlEncoder.query("part" to part))

    suspend fun movePopulation(sourceId: Int, targetId: Int): City = client.json.decodeFromString(
        client.send(
            "POST",
            "/special/move-population" + UrlEncoder.query("sourceId" to sourceId, "targetId" to targetId)
        )
    )

    suspend fun movePopulationToSmallest(sourceId: Int): City = client.json.decodeFromString(
        client.send(
            "POST",
            "/special/move-population-to-smallest" + UrlEncoder.query("sourceId" to sourceId)
        )
    )
}
