package com.example.songlist

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.example.common.CoroutineDispatcher
import com.example.domain.model.Result
import com.example.domain.model.Song
import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.ISortSongsUsecase
import com.example.songlist.viewmodel.SongListState
import com.example.songlist.viewmodel.SongListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


@OptIn(ExperimentalCoroutinesApi::class)
class SongListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var getSongsUsecase: IGetSongsUsecase
    @MockK
    private lateinit var filterSongsUsecase: IFilterSongsUsecase
    @MockK
    private lateinit var sortSongsUsecase: ISortSongsUsecase

    private lateinit var coroutineDispatcher: CoroutineDispatcher

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: SongListViewModel

    val fullList = listOf(
        SongModelUtil.createSong("A", albumName = "C"),
        SongModelUtil.createSong("C", albumName = "A"),
        SongModelUtil.createSong("B", albumName = "B"),
        SongModelUtil.createSong("D", albumName = "D"),
    )
    val loadMoreList = fullList + fullList
    val filteredListA = listOf(
        SongModelUtil.createSong("A", albumName = "C"),
        SongModelUtil.createSong("C", albumName = "A")
    )
    val filteredListB = listOf(
        SongModelUtil.createSong("B", albumName = "B")
    )
    val sortedSongList = listOf(
        SongModelUtil.createSong("A", albumName = "C"),
        SongModelUtil.createSong("B", albumName = "B"),
        SongModelUtil.createSong("C", albumName = "A"),
        SongModelUtil.createSong("D", albumName = "D")
    )
    val sortedAlbumList = listOf(
        SongModelUtil.createSong("C", albumName = "A"),
        SongModelUtil.createSong("B", albumName = "B"),
        SongModelUtil.createSong("A", albumName = "C"),
        SongModelUtil.createSong("D", albumName = "D")
    )
    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coroutineDispatcher = CoroutineDispatcher(
            testDispatcher,
            testDispatcher,
            testDispatcher
        )

        coEvery {
            getSongsUsecase.invoke(any())
        } returns Result.Success(fullList)

        viewModel = SongListViewModel(
            coroutineDispatcher,
            getSongsUsecase,
            filterSongsUsecase,
            sortSongsUsecase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
    private suspend fun TurbineTestContext<SongListState>.consumeInitialEmit() {
        // consume initial full list item emitted
        assertEquals(persistentListOf<Song>(), awaitItem().songList)
    }
    @Test
    fun `get full song list success`() = runTest {
        coEvery {
            filterSongsUsecase.invoke(any(), any())
        } returns fullList
        coEvery {
            sortSongsUsecase.invoke(any(), any())
        } returns fullList

        viewModel.uiState.test {
            this.consumeInitialEmit()
            val result = awaitItem().songList
            assertEquals(result, fullList)
        }
    }

    @Test
    fun `get filtered song list success`() = runTest {
        coEvery {
            filterSongsUsecase.invoke(any(), any())
        } returns filteredListA
        coEvery {
            sortSongsUsecase.invoke(any(), any())
        } returns filteredListA
        viewModel.onSearch("A")
        viewModel.uiState.test {
            this.consumeInitialEmit()
            val result = awaitItem().songList
            assertEquals(result, filteredListA)
        }
    }
    @Test
    fun `filter song continuously`() = runTest {
        val keywordA = "A"
        val keywordB = "B"
        coEvery {
            filterSongsUsecase.invoke(any(), keywordA)
        } returns filteredListA
        coEvery {
            filterSongsUsecase.invoke(any(), keywordB)
        } returns filteredListB
        coEvery {
            sortSongsUsecase.invoke(filteredListA, any())
        } returns filteredListA
        coEvery {
            sortSongsUsecase.invoke(filteredListB, any())
        } returns filteredListB

        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onSearch(keywordA)
            val result1UIChange = awaitItem()
            assertEquals(keywordA, result1UIChange.searchKeyword)
            val result1ActualFilter = awaitItem()
            assertEquals(filteredListA, result1ActualFilter.songList)

            viewModel.onSearch(keywordB)
            val result2UIChange = awaitItem()
            assertEquals(keywordB, result2UIChange.searchKeyword)
            val result2ActualFilter = awaitItem()
            assertEquals(filteredListB, result2ActualFilter.songList)

            viewModel.onSearch(keywordA)
            val result3UIChange = awaitItem()
            assertEquals(keywordA, result3UIChange.searchKeyword)
            val result3ActualFilter = awaitItem()
            assertEquals(filteredListA, result3ActualFilter.songList)
        }
    }

    @Test
    fun `switching sorting between song and album`() = runTest {
        coEvery {
            getSongsUsecase.invoke(any())
        } returns Result.Success(fullList)
        coEvery {
            filterSongsUsecase.invoke(any(), any())
        } returns fullList
        coEvery {
            sortSongsUsecase.invoke(any(), SongSortingColumn.SONG_NAME)
        } returns sortedSongList
        coEvery {
            sortSongsUsecase.invoke(any(), SongSortingColumn.ALBUM_NAME)
        } returns sortedAlbumList

        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val result1UIChange = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, result1UIChange.sortingColumn)
            val result1ActualSort = awaitItem()
            assertEquals(sortedAlbumList, result1ActualSort.songList)

            viewModel.onSelectSorting(SongSortingColumn.SONG_NAME)
            val result2UIChange = awaitItem()
            assertEquals(SongSortingColumn.SONG_NAME, result2UIChange.sortingColumn)
            val result2ActualSorted = awaitItem()
            assertEquals(sortedSongList, result2ActualSorted.songList)

            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val result3UIChange = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, result3UIChange.sortingColumn)
            val result3ActualSorted = awaitItem()
            assertEquals(sortedAlbumList, result3ActualSorted.songList)
        }
    }

    @Test
    fun `loadMore`() = runTest {
        coEvery {
            getSongsUsecase.invoke(100)
        } returns Result.Success(fullList)
        coEvery {
            filterSongsUsecase.invoke(fullList, any())
        } returns fullList
        coEvery {
            sortSongsUsecase.invoke(fullList, any())
        } returns fullList
        coEvery {
            getSongsUsecase.invoke(200)
        } returns Result.Success(loadMoreList)
        coEvery {
            filterSongsUsecase.invoke(loadMoreList, any())
        } returns loadMoreList
        coEvery {
            sortSongsUsecase.invoke(loadMoreList, any())
        } returns loadMoreList

        viewModel.uiState.test {
            this.consumeInitialEmit()
            val default = awaitItem()
            assertEquals(100, default.limit)
            assertEquals(fullList, default.songList)
            viewModel.onLoadMore()
            val result1UIChange = awaitItem()
            assertEquals(200, result1UIChange.limit)
            val result1ActualLoadMore = awaitItem()
            assertEquals(loadMoreList, result1ActualLoadMore.songList)
        }
    }
}