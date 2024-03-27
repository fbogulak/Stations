package com.example.stations.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stations: List<DatabaseStation>)

    @Query("SELECT * FROM stations ORDER BY hits DESC")
    fun getStations(): LiveData<List<DatabaseStation>>

    @Query("DELETE FROM stations")
    fun deleteAllStations()
}