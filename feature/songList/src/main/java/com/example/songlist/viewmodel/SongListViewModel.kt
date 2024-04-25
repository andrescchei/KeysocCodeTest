package com.example.songlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.CoroutineDispatcher
import com.example.domain.model.Result
import com.example.domain.model.GetSongsError
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
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

    private val actionFlow = MutableStateFlow<SongListEvent>(SongListEvent.OnStart)
    private val originalListFlow = MutableStateFlow<List<Song>>(emptyList())

    val uiState: StateFlow<SongListState> = actionFlow.debounce(300L)
        .scan(SongListState()) { accuState, event ->
            return@scan when (event) {
                SongListEvent.OnStart -> {
                    val newResult = getSongList(pageSize)
                    songListResultToSongListState(
                        accuState.copy(
                            limit = pageSize,
                            isLastPage = false
                        ), newResult
                    )
                }
                SongListEvent.OnLoadMore -> {
                    if (accuState.limit >= maxSize) {
                        return@scan accuState
                    }
                    val newLimit = accuState.limit + pageSize
                    val newResult = getSongList(newLimit)
                    songListResultToSongListState(
                        accuState.copy(
                            limit = newLimit,
                            isLastPage = (newLimit) >= maxSize
                        ), newResult
                    )
                }

                is SongListEvent.OnSearch -> {
                    val newState = accuState.copy(searchKeyword = event.searchText)
                    val finalList = filterAndSortSongList(originalListFlow.value, newState.sortingColumn, newState.searchKeyword)
                    newState.copy(songList = finalList.toImmutableList())
                }

                is SongListEvent.OnSelectSorting -> {
                    val newState = accuState.copy(sortingColumn = event.sortingColumn)
                    val finalList = filterAndSortSongList(originalListFlow.value, newState.sortingColumn, newState.searchKeyword)
                    newState.copy(songList = finalList.toImmutableList())
                }

                SongListEvent.OnToasted -> accuState.copy(toastMessage = null)
            }

        }
        .flowOn(coroutineDispatcher.io).stateIn(viewModelScope, SharingStarted.Lazily, SongListState())

    private suspend fun filterAndSortSongList(list: List<Song>, sortingColumn: SongSortingColumn, keyword: String): List<Song> {
        val filteredList = filterSongsUsecase(list, keyword)
        return sortSongsUsecase(filteredList, sortingColumn)
    }
    private suspend fun getSongList(limit: Int): Result<List<Song>, GetSongsError> {
        return getSongListUsecase(limit)
    }

    private suspend fun songListResultToSongListState(state: SongListState, result: Result<List<Song>, GetSongsError>): SongListState {
        return when(result) {
            is Result.Error -> {
                when(val error = result.error) {
                    is GetSongsError.Unknown -> state.copy(toastMessage = error.errorMessage)
                }
            }
            is Result.Success -> {
                originalListFlow.update { result.response }
                val finalList = filterAndSortSongList(result.response, state.sortingColumn, state.searchKeyword)
                state.copy(songList = finalList.toImmutableList())
            }
        }
    }

    fun onSelectSorting(sortingColumn: SongSortingColumn) {
        actionFlow.update { SongListEvent.OnSelectSorting(sortingColumn) }
    }

    fun onSearch(keyword: String) {
        actionFlow.update { SongListEvent.OnSearch(keyword) }
    }

    fun onLoadMore() {
        actionFlow.update { SongListEvent.OnLoadMore }
    }

    fun onToasted() {
        actionFlow.update { SongListEvent.OnToasted }
    }

    fun onEvent(event: SongListEvent) {
        actionFlow.update { event }
    }
}