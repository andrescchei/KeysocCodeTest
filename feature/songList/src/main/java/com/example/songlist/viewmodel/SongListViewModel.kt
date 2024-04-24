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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SongListViewModel(
    private val getSongListUsecase: IGetSongsUsecase,
    private val filterSongsUsecase: IFilterSongsUsecase,
    private val sortSongsUsecase: ISortSongsUsecase
): ViewModel() {

    private val originalListFlow = MutableStateFlow<List<Song>>(listOf())
    private val limitFlow = MutableStateFlow(100)

    private val _uiState = MutableStateFlow(SongListState())
    val uiState: StateFlow<SongListState> = _uiState

    private val maxSize = 200
    private val pageSize = 100
    init {
        viewModelScope.launch {
            limitFlow.debounce(timeoutMillis = 1000L).collectLatest {
                when(val result = getSongListUsecase.invoke(it)) {
                    is Result.Success -> originalListFlow.update { result.response }
                    is Result.Error -> { println("${result.error}") }//TODO Toast
                }
            }
        }

        viewModelScope.launch {
            originalListFlow.combine(_uiState) { origin, state ->
                val filteredList = filterSongsUsecase.invoke(origin, state.searchKeyword)
                val sortedList = sortSongsUsecase.invoke(filteredList, state.sortingColumn)
                _uiState.update {
                    it.copy(songList = sortedList.toImmutableList(), isLastPage = origin.size == maxSize)
                }
            }.collect()
        }
    }

    fun onSelectSorting(sortingColumn: SongSortingColumn) {
        _uiState.update {
            it.copy(sortingColumn = sortingColumn)
        }
    }

    fun onSearch(keyword: String) {
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
    }

    fun onLoadMore() {
        if(!_uiState.value.isLastPage) {
            viewModelScope.launch {
                limitFlow.update { it + pageSize }
            }
        }
    }
}