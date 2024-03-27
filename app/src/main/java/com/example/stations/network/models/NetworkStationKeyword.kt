package com.example.stations.network.models

import com.example.stations.database.DatabaseStationKeyword
import com.squareup.moshi.Json

data class NetworkStationKeyword(
    val id: Int,
    val keyword: String,
    @Json(name = "station_id") val stationId: Int,
)
fun List<NetworkStationKeyword>.asDatabaseModel() = map {
    DatabaseStationKeyword(
        it.id,
        it.keyword,
        it.stationId,
    )
}