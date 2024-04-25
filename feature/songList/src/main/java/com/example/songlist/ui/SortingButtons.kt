package com.example.songlist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.domain.model.SongSortingColumn
import com.example.songlist.viewmodel.SongListEvent

@Composable
fun SortingButtons(sortingColumn: SongSortingColumn, onEvent: (SongListEvent) -> Unit) {
    var sort = remember { mutableStateOf(sortingColumn) }
    Row {
        TextButton(
            onClick = {
                onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.SONG_NAME))
                sort.value = SongSortingColumn.SONG_NAME
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = if(sort.value == SongSortingColumn.SONG_NAME) Color.Blue else Color.LightGray
            )
        ) {
            Text(text = "Song")
        }
        TextButton(
            onClick = {
                onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.ALBUM_NAME))
                sort.value = SongSortingColumn.ALBUM_NAME
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = if(sort.value == SongSortingColumn.ALBUM_NAME) Color.Blue else Color.LightGray
            )
        ) {
            Text(text = "Album")
        }
    }
}
