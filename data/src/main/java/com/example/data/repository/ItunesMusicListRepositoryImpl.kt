package com.example.data.repository

import com.example.data.datasource.IItunesMusicListDatasource
import com.example.data.model.ItunesMusic
import com.example.data.model.Result
import retrofit2.Response

internal class ItunesMusicListRepositoryImpl(private val datasource: IItunesMusicListDatasource): IItunesMusicListRepository {
    override suspend fun getItunesMusicList(
        keyword: String,
        limit: Int
    ): Response<List<ItunesMusic>> {
        return datasource.getItunesMusicList(keyword, limit)
    }
}