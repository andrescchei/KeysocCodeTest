package com.example.songlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Result
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SongListViewModel(
    private val getSongListUsecase: IGetSongsUsecase,
    private val filterSongsUsecase: IFilterSongsUsecase,
    private val sortSongsUsecase: ISortSongsUsecase
): ViewModel() {

    private val originalListFlow = MutableStateFlow<List<Song>>(listOf())
    private val limitFlow = MutableStateFlow(100)

    private val _uiState = MutableStateFlow(SongListState())
    val uiState: StateFlow<SongListState> = _uiState

    init {
        viewModelScope.launch {
            limitFlow.collectLatest {
                when(val result = getSongListUsecase.invoke(it)) {
                    is Result.Success -> originalListFlow.update { result.response }
                    is Result.Error -> { }//TODO Toast
                }
            }
        }

        viewModelScope.launch {
            originalListFlow.combine(_uiState) { origin, state ->
                val filteredList = filterSongsUsecase.invoke(origin, state.searchKeyword)
                val sortedList = sortSongsUsecase.invoke(filteredList, state.sortingColumn)
                _uiState.update {
                    it.copy(songList = sortedList.toImmutableList())
                }
            }.collect()
        }
    }

    fun OnSelectSorting(sortingColumn: SongSortingColumn) {
        _uiState.update {
            it.copy(sortingColumn = sortingColumn)
        }
    }

    fun OnSearch(keyword: String) {
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
    }

    fun OnLoadMore() {
        limitFlow.update { 200 }
    }
}