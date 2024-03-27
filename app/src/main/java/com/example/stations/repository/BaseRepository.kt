package com.example.stations.repository

import androidx.lifecycle.LiveData
import com.example.stations.domain.models.Station
import com.example.stations.domain.models.StationKeyword

interface BaseRepository {
    val stations: LiveData<List<Station>>
    val stationKeywords: LiveData<List<StationKeyword>>
    suspend fun refreshStationsData()
}