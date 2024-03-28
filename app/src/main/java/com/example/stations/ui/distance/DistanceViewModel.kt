package com.example.stations.ui.distance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stations.R
import com.example.stations.domain.models.Station
import com.example.stations.repository.BaseRepository
import com.example.stations.ui.base.BaseViewModel
import com.example.stations.utils.LoadingStatus
import kotlinx.coroutines.launch

class DistanceViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _stations: List<Station> = emptyList()
    val stations: List<Station>
        get() = _stations

    val lastRefreshTime = repository.getLastRefreshTime()
    val fromStation = MutableLiveData<Station>()
    val toStation = MutableLiveData<Station>()

    suspend fun getStations(query: String?) {
        _stations = repository.getStations(query)
    }

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