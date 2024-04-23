package com.example.domain.usecase

import com.example.domain.model.Result
import com.example.domain.model.SearchSongsError
import com.example.domain.model.Song

interface IGetSongsUsecase {
    suspend fun invoke(limit: Int): Result<List<Song>, SearchSongsError>
}