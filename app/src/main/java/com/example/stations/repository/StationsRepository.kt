package com.example.stations.repository

import android.content.SharedPreferences
import androidx.lifecycle.map
import com.example.stations.constants.SAVED_LAST_REFRESH_TIME_KEY
import com.example.stations.database.StationsDatabase
import com.example.stations.database.asDomainModel
import com.example.stations.network.StationsApi
import com.example.stations.network.models.asDatabaseModel
import com.example.stations.utils.toIsoString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class StationsRepository(
    private val database: StationsDatabase,
    private val sharedPref: SharedPreferences,
) : BaseRepository {
    override val stations = database.stationDao.getStations().map { it.asDomainModel() }
    override val stationKeywords =
        database.stationKeywordDao.getStationKeywords().map { it.asDomainModel() }

    override suspend fun refreshStationsData() {
        withContext(Dispatchers.IO) {
            val stations = StationsApi.retrofitService.getNetworkStations().asDatabaseModel()
            val stationKeywords =
                StationsApi.retrofitService.getNetworkStationKeywords().asDatabaseModel()
            database.stationDao.deleteAllStations()
            database.stationKeywordDao.deleteAllStationKeywords()
            database.stationDao.insert(stations)
            database.stationKeywordDao.insert(stationKeywords)
            saveLastRefreshTime(LocalDateTime.now().toIsoString())
        }
    }

    override fun getLastRefreshTime() =
        sharedPref.getString(SAVED_LAST_REFRESH_TIME_KEY, null)

    override fun saveLastRefreshTime(lastRefreshTime: String) {
        with(sharedPref.edit()) {
            putString(SAVED_LAST_REFRESH_TIME_KEY, lastRefreshTime)
            apply()
        }
    }
}