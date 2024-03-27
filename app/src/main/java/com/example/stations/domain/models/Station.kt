package com.example.stations.domain.models

data class Station(
    val id: Int,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val hits: Int,
)