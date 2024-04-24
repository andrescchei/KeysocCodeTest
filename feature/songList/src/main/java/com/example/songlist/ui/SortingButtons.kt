package com.example.songlist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.domain.model.SongSortingColumn
import com.example.songlist.viewmodel.SongListEvent

@Composable
fun SortingButtons(sortingColumn: SongSortingColumn, onEvent: (SongListEvent) -> Unit) {
    Row {
        TextButton(
            onClick = {
                onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.SONG_NAME))
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = if(sortingColumn == SongSortingColumn.SONG_NAME) Color.Blue else Color.LightGray
            )
        ) {
            Text(text = "Song")
        }
        TextButton(
            onClick = {
                onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.ALBUM_NAME))
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = if(sortingColumn == SongSortingColumn.ALBUM_NAME) Color.Blue else Color.LightGray
            )
        ) {
            Text(text = "Album")
        }
    }
}
