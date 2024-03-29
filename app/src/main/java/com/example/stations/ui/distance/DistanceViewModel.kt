package com.example.stations.ui.distance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stations.R
import com.example.stations.domain.models.Station
import com.example.stations.repository.BaseRepository
import com.example.stations.ui.base.BaseViewModel
import com.example.stations.utils.LoadingStatus
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceViewModel(private val repository: BaseRepository) : BaseViewModel() {

    private var _stations: List<Station> = emptyList()
    val stations: List<Station>
        get() = _stations

    val lastRefreshTime = repository.getLastRefreshTime()
    val fromStation = MutableLiveData<Station>()
    val toStation = MutableLiveData<Station>()
    val distance = MutableLiveData<Double>()

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

    fun calculateDistance() {
        val lat1 = fromStation.value?.latitude
        val lon1 = fromStation.value?.longitude
        val lat2 = toStation.value?.latitude
        val lon2 = toStation.value?.longitude
        if (lat1 != null && lon1 != null && lat2 != null && lon2 != null) {
            val r = 6371 // Radius of the earth in km
            val dLat = deg2rad(lat2 - lat1)
            val dLon = deg2rad(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = r * c
            distance.value = d
        }
    }

    private fun deg2rad(deg: Double) = deg * (Math.PI / 180)
}