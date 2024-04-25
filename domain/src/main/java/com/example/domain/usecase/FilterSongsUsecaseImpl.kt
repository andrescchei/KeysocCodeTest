package com.example.domain.usecase

import com.example.domain.model.Song

class FilterSongsUsecaseImpl: IFilterSongsUsecase {
    override suspend operator fun invoke(list: List<Song>, keyword: String): List<Song> {
        return list.filter { it.songName.contains(keyword) or it.albumName.contains(keyword) }
    }
}