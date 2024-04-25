package com.example.domain.usecase

import com.example.domain.model.Result
import com.example.domain.model.GetSongsError
import com.example.domain.model.Song

interface IGetSongsUsecase {
    suspend operator fun invoke(limit: Int): Result<List<Song>, GetSongsError>
}