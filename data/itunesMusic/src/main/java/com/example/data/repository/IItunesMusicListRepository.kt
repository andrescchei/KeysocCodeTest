package com.example.data.repository

import com.example.data.model.ItunesSearchResult
import retrofit2.Response

interface IItunesMusicListRepository {
    suspend fun getItunesMusicList(limit: Int): Response<ItunesSearchResult>
}