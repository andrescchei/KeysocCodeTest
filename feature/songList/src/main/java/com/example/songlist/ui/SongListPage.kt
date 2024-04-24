package com.example.songlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            SongListEvent.OnLoadMore -> viewModel.onLoadMore()
            is SongListEvent.OnSearch -> viewModel.onSearch(event.searchText)
            is SongListEvent.OnSelectSorting -> viewModel.onSelectSorting(event.sortingColumn)
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
                        onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.SONG_NAME))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if(isSortbySong) Color.Blue else Color.LightGray
                    )
                ) {
                    Text(text = "Song")
                }
                TextButton(
                    onClick = {
                        onEvent(SongListEvent.OnSelectSorting(SongSortingColumn.ALBUM_NAME))
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
        SongList(songs = uiState.songList, isLastPage = uiState.isLastPage, onEvent = onEvent)
    }
}

@Composable
fun SongList(songs: ImmutableList<Song>, isLastPage: Boolean, onEvent: (SongListEvent) -> Unit) {
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
                    Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(40.dp),
                        )
                    }
                    LaunchedEffect(Unit) {
                        onEvent(SongListEvent.OnLoadMore)
                    }
                }
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
            Text(song.songName,
                modifier = Modifier.fillMaxHeight(),
                fontSize = 18.sp)
            Text(song.albumName,
                fontSize = 12.sp)
        }
    }
    Divider(thickness = 1.dp, color = Color.Black)
}