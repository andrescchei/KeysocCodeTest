package com.example.songlist.ui

import LoadingItem
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.domain.model.Song
import com.example.songlist.viewmodel.SongListEvent
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SongList(songs: ImmutableList<Song>, isLastPage: Boolean, onEvent: (SongListEvent) -> Unit) {
    println("Song list recompose $songs")
    if(songs.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
        ) {
            items(songs) { item ->
                SongItem(item)
            }
            if(!isLastPage) {
                item {
                    LoadingItem(onEvent = onEvent)
                }
            }
        }
    }
}