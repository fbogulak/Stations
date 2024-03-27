package com.example.stations.repository

import androidx.lifecycle.map
import com.example.stations.database.StationsDatabase
import com.example.stations.database.asDomainModel
import com.example.stations.network.StationsApi
import com.example.stations.network.models.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationsRepository(private val database: StationsDatabase) : BaseRepository {
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
        }
    }
}