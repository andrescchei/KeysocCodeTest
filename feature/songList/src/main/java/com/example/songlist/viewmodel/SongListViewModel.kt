package com.example.songlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.Result
import com.example.domain.model.SearchSongsError
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.skip
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SongListViewModel(
    private val getSongListUsecase: IGetSongsUsecase,
    private val filterSongsUsecase: IFilterSongsUsecase,
    private val sortSongsUsecase: ISortSongsUsecase
): ViewModel() {

    private val limitFlow = MutableStateFlow(100)
    private val sortingFlow = MutableStateFlow(SongSortingColumn.SONG_NAME)
    private val searchFlow = MutableStateFlow("")

    private val _uiState = MutableStateFlow(SongListState())
    val uiState: StateFlow<SongListState> = _uiState

    private val maxSize = 200
    private val pageSize = 100
    init {
        viewModelScope.launch {
            combine(limitFlow, sortingFlow, searchFlow) { limit, sort, search ->
                _uiState.value.copy(
                    limit = limit,
                    sortingColumn = sort,
                    searchKeyword = search
                )
            }.debounce(timeoutMillis = 300L)
            .map { state ->
                when (val result = getSongListUsecase.invoke(state.limit)) {
                    is Result.Success -> {
                        val filtered = filterSongsUsecase.invoke(result.response, state.searchKeyword).toImmutableList()
                        val sorted = sortSongsUsecase.invoke(filtered, state.sortingColumn).toImmutableList()
                        state.copy(
                            songList = sorted.toImmutableList(),
                            isLastPage = result.response.size == maxSize
                        )
                    }
                    is Result.Error ->
                        when (val error = result.error) {
                            is SearchSongsError.Unknown -> state.copy(toastMessage = error.errorMessage)
                        }
                }
            }.collectLatest { state ->
                _uiState.update {
                    state
                }
            }
        }
    }

    fun onSelectSorting(sortingColumn: SongSortingColumn) {
        sortingFlow.update { sortingColumn }
        _uiState.update {
            it.copy(sortingColumn = sortingColumn)
        }
    }

    fun onSearch(keyword: String) {
        searchFlow.update { keyword }
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

    fun onToasted() {
        _uiState.update {
            it.copy(toastMessage = null)
        }
    }
}