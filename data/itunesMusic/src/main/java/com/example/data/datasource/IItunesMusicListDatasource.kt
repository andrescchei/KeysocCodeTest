package com.example.data.datasource

import com.example.data.model.ItunesMusic
import retrofit2.Response

interface IItunesMusicListDatasource {
    suspend fun getItunesMusicList(limit: Int): Response<List<ItunesMusic>>
}