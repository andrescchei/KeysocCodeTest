package com.example.songlist.viewmodel

import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf

data class SongListState (
    val searchKeyword: String =  "",
    val songList: ImmutableList<Song> = persistentListOf(),
    val sortingColumn: SongSortingColumn = SongSortingColumn.NONE
)

sealed interface SongListEvent {
    data class OnSearch(
        val searchText: String
    ): SongListEvent

    data class OnSelectSorting(
        val sortingColumn: SongSortingColumn
    ): SongListEvent

    data object OnLoadMore: SongListEvent
}