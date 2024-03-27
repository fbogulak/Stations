package com.example.stations.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseStation::class, DatabaseStationKeyword::class],
    version = 1
)
abstract class StationsDatabase : RoomDatabase() {

    abstract val stationDao: StationDao
    abstract val stationKeywordDao: StationKeywordDao

    companion object {

        @Volatile
        private var INSTANCE: StationsDatabase? = null

        fun getInstance(context: Context): StationsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StationsDatabase::class.java,
                        "stations_database"
                    )
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}