package com.example.songlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.CoroutineDispatcher
import com.example.domain.model.Result
import com.example.domain.model.SearchSongsError
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
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

@OptIn(FlowPreview::class)
class SongListViewModel(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val getSongListUsecase: IGetSongsUsecase,
    private val filterSongsUsecase: IFilterSongsUsecase,
    private val sortSongsUsecase: ISortSongsUsecase
): ViewModel() {
    private val _uiState = MutableStateFlow(SongListState())

    private val _limitFlow = MutableStateFlow(_uiState.value.limit)
    private val _sortingFlow = MutableStateFlow(_uiState.value.sortingColumn)
    private val _searchFlow = MutableStateFlow(_uiState.value.searchKeyword)

    private val limitFlow = _limitFlow
        .map {
            val newState = when (val result = getSongListUsecase.invoke(it)) {
                is Result.Success -> {
                    _uiState.value.copy(
                        songList = result.response.toImmutableList(),
                        isLastPage = result.response.size == maxSize
                    )
                }

                is Result.Error ->
                    when (val error = result.error) {
                        is SearchSongsError.Unknown -> _uiState.value.copy(toastMessage = error.errorMessage)
                    }
            }
            newState
        }
        .debounce(timeoutMillis = 300L)
        .distinctUntilChanged()
    private val sortingFlow = _sortingFlow.debounce(300L).distinctUntilChanged()
    private val searchFlow = _searchFlow.debounce(300L).distinctUntilChanged()

    private val mainFlow =
        combine(limitFlow, sortingFlow, searchFlow) { state, sort, search ->
            val filtered = filterSongsUsecase.invoke(state.songList, search).toImmutableList()
            val sorted = sortSongsUsecase.invoke(filtered, sort).toImmutableList()
            val newState = _uiState.value.copy(
                songList = sorted.toImmutableList(),
                isLastPage = state.songList.size == maxSize
            )
            _uiState.update {
                newState
            }
            newState
        }.flowOn(coroutineDispatcher.io)

    val uiState: StateFlow<SongListState> = merge(_uiState, mainFlow).distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Lazily, SongListState())


    private val maxSize = 200
    private val pageSize = 100

    fun onSelectSorting(sortingColumn: SongSortingColumn) {
        _sortingFlow.update {
            sortingColumn
        }
        _uiState.update {
            it.copy(sortingColumn = sortingColumn)
        }
    }

    fun onSearch(keyword: String) {
        _searchFlow.update {
            keyword
        }
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
    }

    fun onLoadMore() {
        if(!_uiState.value.isLastPage) {
            val limit = _uiState.value.limit
            _limitFlow.update {
                limit + pageSize
            }
            _uiState.update {
                it.copy(limit = limit + pageSize)
            }
        }
    }

    fun onToasted() {
        _uiState.update {
            it.copy(toastMessage = null)
        }
    }
}