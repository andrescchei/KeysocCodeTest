package com.example.domain.usecase

import com.example.data.model.Error
import com.example.data.model.Result
import com.example.domain.model.Song

interface IGetSongsUsecase {
    suspend fun execute(keyword: String, limit: Int): Result<List<Song>, SearchSongsError>

    sealed interface SearchSongsError: Error {
        data class Unknown(val errorMessage: String): SearchSongsError
    }
}