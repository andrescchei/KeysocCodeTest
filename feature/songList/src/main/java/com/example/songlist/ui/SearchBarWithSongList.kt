package com.example.songlist.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.songlist.viewmodel.SongListEvent
import com.example.songlist.viewmodel.SongListState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithSongList(uiState: SongListState, onEvent: (SongListEvent) -> Unit) {
    var keyword = remember { mutableStateOf(uiState.searchKeyword) }
    SearchBar(
        keyword.value,
        onQueryChange = {
            keyword.value = it
            onEvent(SongListEvent.OnSearch(it))
        },
        onSearch = {
            keyword.value = it
            onEvent(SongListEvent.OnSearch(it))
        },
        active = true,
        onActiveChange = {
        },
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(text = "search", color = Color.Gray)
        },
        trailingIcon = {
            SortingButtons(uiState.sortingColumn, onEvent = onEvent)
        }
    ) {
        SongList(songs = uiState.songList, isLastPage = uiState.isLastPage, onEvent = onEvent)
    }
}