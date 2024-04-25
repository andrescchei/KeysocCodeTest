package com.example.songlist.ui

import LoadingItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    } else {
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "No Result",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}