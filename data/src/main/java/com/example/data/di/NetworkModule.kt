package com.example.data.di
import com.example.data.network.provideConverterFactory
import com.example.data.network.provideHttpClient
import com.example.data.network.provideRetrofit
import com.example.data.network.provideService
import org.koin.dsl.module
val networkModule = module {
    single { provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(),get()) }
    single { provideService(get()) }
}