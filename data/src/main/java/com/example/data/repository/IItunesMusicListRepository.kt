package com.example.data.repository

import com.example.data.model.ItunesMusic
import retrofit2.Response

interface IItunesMusicListRepository {
    suspend fun getItunesMusicList(keyword: String, limit: Int): Response<List<ItunesMusic>>
}