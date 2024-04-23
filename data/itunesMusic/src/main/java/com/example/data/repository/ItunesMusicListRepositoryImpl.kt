package com.example.data.repository

import com.example.data.datasource.IItunesMusicListDatasource
import com.example.data.model.ItunesMusic
import retrofit2.Response

internal class ItunesMusicListRepositoryImpl(private val datasource: IItunesMusicListDatasource): IItunesMusicListRepository {
    override suspend fun getItunesMusicList(
        limit: Int
    ): Response<List<ItunesMusic>> {
        return datasource.getItunesMusicList(limit)
    }
}