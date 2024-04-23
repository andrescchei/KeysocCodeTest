package com.example.data.repository

import com.example.data.model.ItunesMusic
import retrofit2.Response

interface IItunesMusicListRepository {
    suspend fun getItunesMusicList(limit: Int): Response<List<ItunesMusic>>
}