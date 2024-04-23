package com.example.data.repository

import com.example.data.datasource.IItunesMusicListDatasource
import com.example.data.model.ItunesMusic
import com.example.data.model.ItunesSearchResult
import retrofit2.Response

internal class ItunesMusicListRepositoryImpl(private val datasource: IItunesMusicListDatasource): IItunesMusicListRepository {
    override suspend fun getItunesMusicList(
        limit: Int
    ): Response<ItunesSearchResult> {
        return datasource.getItunesMusicList(limit)
    }
}