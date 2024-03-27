package com.example.stations.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StationKeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stationKeywords: List<DatabaseStationKeyword>)

    @Query("SELECT * FROM station_keywords")
    fun getStationKeywords(): LiveData<List<DatabaseStationKeyword>>

    @Query("DELETE FROM station_keywords")
    fun deleteAllStationKeywords()
}