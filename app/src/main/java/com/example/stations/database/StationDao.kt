package com.example.stations.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stations: List<DatabaseStation>)

    @Query("SELECT * FROM stations ORDER BY hits DESC")
    fun getStations(): List<DatabaseStation>

    @Query("SELECT * FROM stations INNER JOIN station_keywords ON stations.id = stationId WHERE keyword LIKE :expression ORDER BY hits DESC")
    fun getFilteredStations(expression: String): List<DatabaseStation>

    @Query("DELETE FROM stations")
    fun deleteAllStations()
}