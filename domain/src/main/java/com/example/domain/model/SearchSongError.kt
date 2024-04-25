package com.example.domain.model
sealed interface GetSongsError: Error {
    data class Unknown(val errorMessage: String): GetSongsError
}