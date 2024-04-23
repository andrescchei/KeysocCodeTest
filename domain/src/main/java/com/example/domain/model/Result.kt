package com.example.domain.model

typealias BaseError = Error
sealed interface Result<R, E: BaseError> {
    data class Success<R, E: BaseError>(val response: R): Result<R, E>
    data class Error<R, E: BaseError>(val error: E): Result<R, E>
}