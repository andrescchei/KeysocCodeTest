package com.example.domain.usecase

import com.example.data.model.Result
import com.example.data.model.Result.Error
import com.example.data.repository.IItunesMusicListRepository
import com.example.domain.model.Song
import java.net.URL

internal class GetSongsUsecaseImpl(private val repo: IItunesMusicListRepository): IGetSongsUsecase {
    override suspend fun execute(
        keyword: String,
        limit: Int
    ): Result<List<Song>, IGetSongsUsecase.SearchSongsError> {
        val response = repo.getItunesMusicList(keyword, limit)
        return if(response.isSuccessful) {
            val list = response.body() ?: listOf()
            Result.Success(
                list.map { music ->
                    val url = music.artworkUrl100?.let { URL(it) }
                    Song(
                        music.trackName,
                        music.collectionName,
                        url
                    )
                }
            )
        } else {
            Error(IGetSongsUsecase.SearchSongsError.Unknown(response.message()))
        }
    }

}