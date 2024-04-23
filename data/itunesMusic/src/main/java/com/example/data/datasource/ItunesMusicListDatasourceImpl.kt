package com.example.data.datasource

import com.example.data.model.ItunesMusic
import com.example.data.network.ApiService
import retrofit2.Response

internal class ItunesMusicListDatasourceImpl(
    private val apiService: ApiService
): IItunesMusicListDatasource {
    override suspend fun getItunesMusicList(limit: Int): Response<List<ItunesMusic>> {
        return apiService.searchSongsBy(limit = limit)
    }
}