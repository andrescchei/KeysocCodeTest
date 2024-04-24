package com.example.songlist.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.songlist.viewmodel.SongListEvent
import com.example.songlist.viewmodel.SongListState
import com.example.songlist.viewmodel.SongListViewModel
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongListPage() {
    val viewModel: SongListViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    SearchBarWithSongList(uiState = uiState.value) { event ->
        when(event) {
            SongListEvent.OnLoadMore -> viewModel.OnLoadMore()
            is SongListEvent.OnSearch -> viewModel.OnSearch(event.searchText)
            is SongListEvent.OnSelectSorting -> viewModel.OnSelectSorting(event.sortingColumn)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithSongList(uiState: SongListState, onEvent: (SongListEvent) -> Unit) {
    SearchBar(
        uiState.searchKeyword,
        onQueryChange = {
            onEvent(SongListEvent.OnSearch(it))
        },
        onSearch = {
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
            Row {
                val isSortbySong = uiState.sortingColumn == SongSortingColumn.SONG_NAME
                val isSortbyAlbum = uiState.sortingColumn == SongSortingColumn.ALBUM_NAME
                TextButton(
                    onClick = {
                        val selectColumn =
                            if(isSortbySong) {
                                SongSortingColumn.NONE
                            } else {
                                SongSortingColumn.SONG_NAME
                            }
                        onEvent(SongListEvent.OnSelectSorting(selectColumn))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if(isSortbySong) Color.Blue else Color.LightGray
                    )
                ) {
                    Text(text = "Song", color = Color.LightGray)
                }
                TextButton(
                    onClick = {
                        val selectColumn =
                            if(isSortbyAlbum) {
                                SongSortingColumn.NONE
                            } else {
                                SongSortingColumn.ALBUM_NAME
                            }
                        onEvent(SongListEvent.OnSelectSorting(selectColumn))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if(isSortbyAlbum) Color.Blue else Color.LightGray
                    )
                ) {
                    Text(text = "Album")
                }
            }
        }
    ) {
        SongList(songs = uiState.songList)
    }
}

@Composable
fun SongList(songs: ImmutableList<Song>) {
    if(songs.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
        ) {
            itemsIndexed(
                items = songs,
                key = { _, item ->
                item.id
            }) {index, item ->
                //TODO: loading more indicator after last item
                SongItem(item)
            }
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArt)
                .crossfade(true)
                .build(),
            contentDescription = "album image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Text(song.id)
            Text(song.songName)
            Text(song.albumName)
        }
    }
    Divider(thickness = 1.dp, color = Color.Black)
}