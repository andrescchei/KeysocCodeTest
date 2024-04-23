package com.example.data.network

import com.example.data.model.ItunesMusic
import com.example.data.model.ItunesSearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun searchSongsBy(@Query("term") term: String = "Taylor Swift", @Query("limit") limit: Int, @Query("media") media: String = "music"): Response<ItunesSearchResult>
}