package com.example.songlist.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songlist.viewmodel.SongListEvent
import com.example.songlist.viewmodel.SongListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongListPage() {
    val viewModel: SongListViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val toastMessage = uiState.value.toastMessage
    if(toastMessage != null) {
        Toast.makeText(LocalContext.current, toastMessage, Toast.LENGTH_SHORT).show()
        viewModel.onEvent(SongListEvent.OnToasted)
    }
    SearchBarWithSongList(uiState = uiState.value) { event ->
        viewModel.onEvent(event)
//        when(event) {
//            SongListEvent.OnLoadMore -> viewModel.onLoadMore()
//            is SongListEvent.OnSearch -> viewModel.onSearch(event.searchText)
//            is SongListEvent.OnSelectSorting -> viewModel.onSelectSorting(event.sortingColumn)
//            is SongListEvent.OnToasted -> viewModel.onToasted()
//        }
    }
}


