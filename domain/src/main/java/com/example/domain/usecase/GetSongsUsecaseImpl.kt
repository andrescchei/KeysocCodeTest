package com.example.domain.usecase

import com.example.domain.model.Result
import com.example.domain.model.Result.Error
import com.example.data.repository.IItunesMusicListRepository
import com.example.domain.model.GetSongsError
import com.example.domain.model.Song
import kotlin.coroutines.cancellation.CancellationException

internal class GetSongsUsecaseImpl(private val repo: IItunesMusicListRepository): IGetSongsUsecase {
    override suspend operator fun invoke(
        limit: Int
    ): Result<List<Song>, GetSongsError> = try {
        val response = repo.getItunesMusicList(limit)
        val list = response.body()?.results ?: listOf()
        Result.Success(
            list.map { music ->
                Song(
                    music.trackId,
                    music.trackName,
                    music.collectionName,
                    music.artworkUrl100
                )
            }
        )
    } catch (e: Exception) {
        when(e) {
            //catching CancellationException will block coroutine cancellation
            is CancellationException -> throw e
            else -> Error(GetSongsError.Unknown(e.localizedMessage ?: ""))
        }
    }
}