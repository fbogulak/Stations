package com.example.stations.network

import com.example.stations.constants.BASE_URL
import com.example.stations.network.models.NetworkStation
import com.example.stations.network.models.NetworkStationKeyword
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder().addHeader("X-KOLEO-Version", "1").build()
        chain.proceed(request)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

interface StationsApiService {
    @GET("stations")
    suspend fun getNetworkStations(): List<NetworkStation>

    @GET("station_keywords")
    suspend fun getNetworkStationKeywords(): List<NetworkStationKeyword>
}

object StationsApi {
    val retrofitService: StationsApiService by lazy {
        retrofit.create(StationsApiService::class.java)
    }
}