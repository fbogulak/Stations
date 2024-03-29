package com.example.stations

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.stations.constants.SHARED_PREFERENCE_NAME
import com.example.stations.database.StationsDatabase
import com.example.stations.repository.BaseRepository
import com.example.stations.repository.StationsRepository
import com.example.stations.ui.distance.DistanceViewModel
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StationsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        val appModule = module {
            single { StationsDatabase.getInstance(this@StationsApp) }
            single { provideSharedPref(androidApplication()) }
            single { StationsRepository(get(), get()) as BaseRepository }
            viewModel { DistanceViewModel(get()) }
        }
        startKoin {
            androidContext(this@StationsApp)
            modules(appModule)
        }
    }

    private fun provideSharedPref(app: Application): SharedPreferences {
        return app.applicationContext.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
}