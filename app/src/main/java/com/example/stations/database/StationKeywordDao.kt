package com.example.stations.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StationKeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stationKeywords: List<DatabaseStationKeyword>)

    @Query("DELETE FROM station_keywords")
    fun deleteAllStationKeywords()
}