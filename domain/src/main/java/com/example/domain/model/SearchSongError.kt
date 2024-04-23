package com.example.domain.model
sealed interface SearchSongsError: Error {
    data class Unknown(val errorMessage: String): SearchSongsError
}