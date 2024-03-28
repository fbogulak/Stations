package com.example.stations.repository

import com.example.stations.domain.models.Station

interface BaseRepository {
    suspend fun getStations(query: String?): List<Station>
    suspend fun refreshStationsData()
    fun getLastRefreshTime(): String?
    fun saveLastRefreshTime(lastRefreshTime: String)
}