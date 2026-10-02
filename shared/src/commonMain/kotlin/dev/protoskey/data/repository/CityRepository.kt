package dev.protoskey.data.repository

import dev.protoskey.data.model.City
import dev.protoskey.data.model.CityQuery
import dev.protoskey.data.model.Page
import dev.protoskey.data.network.RestClient
import dev.protoskey.data.utils.UrlEncoder

class CityRepository(private val client: RestClient) {
    suspend fun page(query: CityQuery): Page<City> = client.get(
        "/cities" + UrlEncoder.query(
            "filterField" to query.filterField.takeIf { query.filterValue.isNotBlank() },
            "filterValue" to query.filterValue.takeIf { it.isNotBlank() },
            "sortField" to query.sortField,
            "ascending" to query.ascending,
            "page" to query.page,
            "pageSize" to query.pageSize
        )
    )

    suspend fun byId(id: Int): City = client.get("/cities/$id")

    suspend fun create(city: City): City = client.post("/cities", city)

    suspend fun update(id: Int, city: City): City = client.put("/cities/$id", city)

    suspend fun delete(id: Int) {
        client.send("DELETE", "/cities/$id")
    }
}
