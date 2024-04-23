package com.example.data.network

import com.example.data.model.ItunesMusic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("search")
    suspend fun searchSongsBy(@Path("term") term: String = "Taylor Swift", @Path("limit") limit: Int, @Path("media") media: String = "music"): Response<List<ItunesMusic>>
}