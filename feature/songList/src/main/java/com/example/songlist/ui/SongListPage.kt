package com.example.songlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.domain.model.Song
import com.example.songlist.viewmodel.SongListState
import com.example.songlist.viewmodel.SongListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongListPage() {
    val viewModel: SongListViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    SongList(uiState = uiState.value)
}

@Composable
fun SongList(uiState: SongListState) {
    if(uiState.songList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
        ) {
            items(items = uiState.songList) {
                SongItem(it)
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
        println("DLLM $song.albumArt")
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArt)
                .crossfade(true)
                .build(),
            contentDescription = "album image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape).size(50.dp)
        )
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Text("Song Name: " + song.songName)
            Text("Album Name: " +song.albumName)
        }
    }
}