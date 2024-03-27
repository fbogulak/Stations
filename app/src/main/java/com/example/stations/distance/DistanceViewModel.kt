package com.example.stations.distance

import androidx.lifecycle.viewModelScope
import com.example.stations.R
import com.example.stations.repository.BaseRepository
import com.example.stations.ui.base.BaseViewModel
import com.example.stations.utils.LoadingStatus
import kotlinx.coroutines.launch

class DistanceViewModel(private val repository: BaseRepository) : BaseViewModel() {

    val stations = repository.stations
    val stationKeywords = repository.stationKeywords
    val lastRefreshTime = repository.getLastRefreshTime()

    fun refreshData() {
        mutableStatus.value = LoadingStatus.LOADING
        viewModelScope.launch {
            try {
                repository.refreshStationsData()
                mutableStatus.value = LoadingStatus.SUCCESS
            } catch (e: Exception) {
                setError(e.message, R.string.error_loading_data)
            }
        }
    }
}