package com.example.songlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.CoroutineDispatcher
import com.example.domain.model.Result
import com.example.domain.model.SearchSongsError
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SongListViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getSongListUsecase: IGetSongsUsecase,
    private val filterSongsUsecase: IFilterSongsUsecase,
    private val sortSongsUsecase: ISortSongsUsecase
): ViewModel() {

    private val maxSize = 200
    private val pageSize = 100

    private val originalListFlow = MutableStateFlow<List<Song>>(listOf())

    private val _uiState = MutableStateFlow(SongListState(limit = pageSize))
    val uiState: StateFlow<SongListState> = _uiState

    private var job: Job? = null
    init {
        cancelAndReassignJob(fetchWholeListJob())
    }
    private suspend fun filterAndSortSongList(sortingColumn: SongSortingColumn, keyword: String): List<Song> {
        val filteredList = filterSongsUsecase(originalListFlow.value, keyword)
        return sortSongsUsecase(filteredList, sortingColumn)
    }
    private suspend fun getSongList() {
        when(val result = getSongListUsecase.invoke(_uiState.value.limit)) {
            is Result.Error ->
                when(val error = result.error) {
                    is SearchSongsError.Unknown -> _uiState.update { it.copy(toastMessage = error.errorMessage) }
                }
            is Result.Success -> {
                originalListFlow.update {
                    result.response
                }
            }
        }
    }

    private fun filterAndSortListJob(sortingColumn: SongSortingColumn, keyword: String): Job = viewModelScope.launch(coroutineDispatcher.io) {
        val list = filterAndSortSongList(sortingColumn, keyword)
        _uiState.update {
            it.copy(songList = list.toImmutableList())
        }
    }
    private fun fetchWholeListJob(): Job = viewModelScope.launch(coroutineDispatcher.io) {
        getSongList()
        val songList = filterAndSortSongList(_uiState.value.sortingColumn, _uiState.value.searchKeyword)
        _uiState.update {
            it.copy(
                songList = songList.toImmutableList(),
                isLastPage = originalListFlow.value.size >= maxSize
            )
        }
    }
    fun onSelectSorting(sortingColumn: SongSortingColumn) {
        _uiState.update {
            it.copy(sortingColumn = sortingColumn)
        }
        cancelAndReassignJob(filterAndSortListJob(sortingColumn, _uiState.value.searchKeyword))
    }

    fun onSearch(keyword: String) {
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
        cancelAndReassignJob(filterAndSortListJob(_uiState.value.sortingColumn, keyword))
    }

    fun onLoadMore() {
        if(!_uiState.value.isLastPage) {
            val limit = _uiState.value.limit
            _uiState.update {
                it.copy(limit = limit + pageSize)
            }
            cancelAndReassignJob(fetchWholeListJob())
        }
    }

    fun onToasted() {
        _uiState.update {
            it.copy(toastMessage = null)
        }
    }

    @Synchronized
    private fun cancelAndReassignJob(newJob: Job) {
        job?.cancel()
        job = newJob
    }
}