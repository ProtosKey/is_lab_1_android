package dev.protoskey.data.repository

import dev.protoskey.data.model.City
import dev.protoskey.data.model.Coordinates
import dev.protoskey.data.model.Human
import dev.protoskey.data.network.RestClient
import dev.protoskey.data.utils.UrlEncoder

class ReferenceRepository(private val client: RestClient) {
    suspend fun coordinates(): List<Coordinates> = client.get("/coordinates")

    suspend fun createCoordinates(coordinates: Coordinates): Coordinates =
        client.post("/coordinates", coordinates)

    suspend fun updateCoordinates(id: Int, coordinates: Coordinates): Coordinates =
        client.put("/coordinates/$id", coordinates)

    suspend fun citiesWithCoordinates(id: Int): List<City> = client.get("/coordinates/$id/cities")

    suspend fun deleteCoordinates(id: Int, replacementId: Int?) {
        client.send("DELETE", "/coordinates/$id" + UrlEncoder.query("replacementId" to replacementId))
    }

    suspend fun humans(): List<Human> = client.get("/humans")

    suspend fun createHuman(human: Human): Human = client.post("/humans", human)

    suspend fun updateHuman(id: Int, human: Human): Human = client.put("/humans/$id", human)

    suspend fun citiesWithGovernor(id: Int): List<City> = client.get("/humans/$id/cities")

    suspend fun deleteHuman(id: Int, replacementId: Int?) {
        client.send("DELETE", "/humans/$id" + UrlEncoder.query("replacementId" to replacementId))
    }
}
