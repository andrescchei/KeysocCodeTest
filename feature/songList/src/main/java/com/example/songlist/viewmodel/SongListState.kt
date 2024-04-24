package com.example.songlist.viewmodel

import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf

data class SongListState (
    val searchKeyword: String =  "",
    val songList: ImmutableList<Song> = persistentListOf(),
    val isLastPage: Boolean = false,
    val sortingColumn: SongSortingColumn = SongSortingColumn.SONG_NAME,
    val toastMessage: String? = null
)

sealed interface SongListEvent {
    data class OnSearch(
        val searchText: String
    ): SongListEvent

    data class OnSelectSorting(
        val sortingColumn: SongSortingColumn
    ): SongListEvent

    data object OnLoadMore: SongListEvent

    data object OnToasted: SongListEvent
}