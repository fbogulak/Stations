package com.example.stations.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.stations.domain.models.StationKeyword

@Entity(
    tableName = "station_keywords", foreignKeys = [ForeignKey(
        entity = DatabaseStation::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stationId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class DatabaseStationKeyword(
    @PrimaryKey
    val id: Int,
    val keyword: String,
    val stationId: Int,
)

fun List<DatabaseStationKeyword>.asDomainModel() = map {
    StationKeyword(
        it.id,
        it.keyword,
        it.stationId,
    )
}