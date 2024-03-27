package com.example.stations.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stations.domain.models.Station

@Entity(tableName = "stations")
data class DatabaseStation(
    @PrimaryKey
    val id: Int,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val hits: Int,
)

fun List<DatabaseStation>.asDomainModel() = map {
    Station(
        it.id,
        it.name,
        it.latitude,
        it.longitude,
        it.hits,
    )
}