package com.example.domain.usecase

import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn

interface ISortSongsUsecase {
    suspend operator fun invoke(list: List<Song>, sorting: SongSortingColumn): List<Song>
}