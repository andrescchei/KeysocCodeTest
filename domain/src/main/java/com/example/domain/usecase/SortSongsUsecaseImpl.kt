package com.example.domain.usecase

import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn

internal class SortSongsUsecaseImpl: ISortSongsUsecase {
    override suspend operator fun invoke(list: List<Song>, sorting: SongSortingColumn): List<Song> {
        return when(sorting) {
            SongSortingColumn.SONG_NAME -> list.sortedBy { it.songName }
            SongSortingColumn.ALBUM_NAME -> list.sortedBy { it.albumName }
        }
    }

}