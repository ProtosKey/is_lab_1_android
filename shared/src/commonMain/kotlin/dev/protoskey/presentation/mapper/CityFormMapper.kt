package dev.protoskey.presentation.mapper

import dev.protoskey.data.model.City
import dev.protoskey.presentation.model.CityForm
import dev.protoskey.presentation.tools.DateMask
import dev.protoskey.presentation.tools.Parser

object CityFormMapper {
    fun toForm(city: City): CityForm = CityForm(
        name = city.name.orEmpty(),
        coordinatesId = city.coordinatesId,
        governorId = city.governorId,
        area = city.area?.toString().orEmpty(),
        population = city.population?.toString().orEmpty(),
        establishmentDate = DateMask.DATE_TIME.digits(city.establishmentDate),
        capital = city.capital ?: false,
        metersAboveSeaLevel = city.metersAboveSeaLevel?.toString().orEmpty(),
        populationDensity = city.populationDensity?.toString().orEmpty(),
        climate = city.climate,
        standardOfLiving = city.standardOfLiving
    )

    /** Возвращает DTO для сервера и ошибки разбора чисел, найденные на клиенте. */
    fun toCity(form: CityForm): Pair<City, Map<String, String>> {
        val parser = Parser.Result()
        val city = City(
            name = form.name.trim(),
            coordinatesId = form.coordinatesId,
            governorId = form.governorId,
            area = parser.int("area", form.area),
            population = parser.int("population", form.population),
            establishmentDate = parser.date("establishmentDate", form.establishmentDate, DateMask.DATE_TIME),
            capital = form.capital,
            metersAboveSeaLevel = parser.float("metersAboveSeaLevel", form.metersAboveSeaLevel, required = false),
            populationDensity = parser.float("populationDensity", form.populationDensity),
            climate = form.climate,
            standardOfLiving = form.standardOfLiving
        )
        return city to parser.errors
    }
}
