package com.example.domain.usecase

import com.example.domain.model.Result
import com.example.domain.model.Result.Error
import com.example.data.repository.IItunesMusicListRepository
import com.example.domain.model.SearchSongsError
import com.example.domain.model.Song
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException

internal class GetSongsUsecaseImpl(private val repo: IItunesMusicListRepository): IGetSongsUsecase {
    override suspend fun invoke(
        limit: Int
    ): Result<List<Song>, SearchSongsError> = try {
        val response = repo.getItunesMusicList(limit)
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
    } catch (e: Exception) {
        when(e) {
            is CancellationException -> throw e
            else -> Error(SearchSongsError.Unknown(e.localizedMessage ?: ""))
        }
    }
}