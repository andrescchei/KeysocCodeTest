package com.example.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val BASE_URL_GET =  "https://itunes.apple.com/"
fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}
fun provideConverterFactory(): GsonConverterFactory =
    GsonConverterFactory.create()
fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL_GET)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()
}
fun provideService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

