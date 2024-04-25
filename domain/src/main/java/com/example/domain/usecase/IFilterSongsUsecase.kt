package com.example.domain.usecase

import com.example.domain.model.Song

interface IFilterSongsUsecase {
    suspend operator fun invoke(list: List<Song>, keyword: String): List<Song>
}