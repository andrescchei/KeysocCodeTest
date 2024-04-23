package com.example.keysoccodetest

import android.app.Application
import com.example.data.di.dataSourceModule
import com.example.data.di.networkModule
import com.example.data.di.repositoryModule
import com.example.domain.di.songUsecaseModule
import com.example.songlist.di.songListModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                networkModule,
                dataSourceModule,
                repositoryModule,
                songUsecaseModule,
                songListModule
            )
        }
    }
}