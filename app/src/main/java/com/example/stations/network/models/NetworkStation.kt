package com.example.stations.network.models

import com.example.stations.database.DatabaseStation

data class NetworkStation(
    val id: Int,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val hits: Int,
)

fun List<NetworkStation>.asDatabaseModel() = map {
    DatabaseStation(
        it.id,
        it.name,
        it.latitude,
        it.longitude,
        it.hits,
    )
}