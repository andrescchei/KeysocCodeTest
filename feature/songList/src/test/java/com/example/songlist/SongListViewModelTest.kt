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


    @MockK
    private lateinit var getSongsUsecase: IGetSongsUsecase
    @MockK
    private lateinit var filterSongsUsecase: IFilterSongsUsecase
    @MockK
    private lateinit var sortSongsUsecase: ISortSongsUsecase

    private lateinit var coroutineDispatcher: CoroutineDispatcher

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

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
            getSongsUsecase(any())
        } returns Result.Success(fullList)
        coEvery {
            filterSongsUsecase(any(), any())
        } returns fullList
        coEvery {
            sortSongsUsecase(any(), any())
        } returns fullList

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
        // consume initial empty item emitted
        assertEquals(fullList, awaitItem().songList)
    }
    @Test
    fun `get full song list success`() = runTest {
        viewModel.uiState.test {
            val result = awaitItem().songList
            assertEquals(result, fullList)
        }
    }

    @Test
    fun `get filtered song list success`() = runTest {
        coEvery {
            filterSongsUsecase(any(), any())
        } returns filteredListA
        coEvery {
            sortSongsUsecase(any(), any())
        } returns filteredListA
        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onSearch("A")
            val result = awaitItem().songList
            assertEquals(result, filteredListA)
        }
    }
    @Test
    fun `filter song continuously`() = runTest {
        val keywordA = "A"
        val keywordB = "B"
        coEvery {
            filterSongsUsecase(any(), keywordA)
        } returns filteredListA
        coEvery {
            filterSongsUsecase(any(), keywordB)
        } returns filteredListB
        coEvery {
            sortSongsUsecase(filteredListA, any())
        } returns filteredListA
        coEvery {
            sortSongsUsecase(filteredListB, any())
        } returns filteredListB

        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onSearch(keywordA)
            val result1UIChange = awaitItem()
            assertEquals(keywordA, result1UIChange.searchKeyword)
            assertEquals(filteredListA, result1UIChange.songList)
//            val result1ActualFilter = awaitItem()
//            assertEquals(filteredListA, result1ActualFilter.songList)

            viewModel.onSearch(keywordB)
            val result2UIChange = awaitItem()
            assertEquals(keywordB, result2UIChange.searchKeyword)
            assertEquals(filteredListB, result2UIChange.songList)
//            val result2ActualFilter = awaitItem()
//            assertEquals(filteredListB, result2ActualFilter.songList)

            viewModel.onSearch(keywordA)
            val result3UIChange = awaitItem()
            assertEquals(keywordA, result3UIChange.searchKeyword)
            assertEquals(filteredListA, result3UIChange.songList)
//            val result3ActualFilter = awaitItem()
//            assertEquals(filteredListA, result3ActualFilter.songList)
        }
    }

    @Test
    fun `switching sorting between song and album`() = runTest {
        coEvery {
            getSongsUsecase(any())
        } returns Result.Success(fullList)
        coEvery {
            filterSongsUsecase(any(), any())
        } returns fullList
        coEvery {
            sortSongsUsecase(any(), SongSortingColumn.SONG_NAME)
        } returns sortedSongList
        coEvery {
            sortSongsUsecase(any(), SongSortingColumn.ALBUM_NAME)
        } returns sortedAlbumList

        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val result1UIChange = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, result1UIChange.sortingColumn)
            assertEquals(sortedAlbumList, result1UIChange.songList)
//            val result1ActualSort = awaitItem()
//            assertEquals(sortedAlbumList, result1ActualSort.songList)

            viewModel.onSelectSorting(SongSortingColumn.SONG_NAME)
            val result2UIChange = awaitItem()
            assertEquals(SongSortingColumn.SONG_NAME, result2UIChange.sortingColumn)
            assertEquals(sortedSongList, result2UIChange.songList)
//            val result2ActualSorted = awaitItem()
//            assertEquals(sortedSongList, result2ActualSorted.songList)

            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val result3UIChange = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, result3UIChange.sortingColumn)
            assertEquals(sortedAlbumList, result3UIChange.songList)

//            val result3ActualSorted = awaitItem()
//            assertEquals(sortedAlbumList, result3ActualSorted.songList)
        }
    }

    @Test
    fun loadMore() = runTest {
        coEvery {
            getSongsUsecase(100)
        } returns Result.Success(fullList)
        coEvery {
            filterSongsUsecase(fullList, any())
        } returns fullList
        coEvery {
            sortSongsUsecase(fullList, any())
        } returns fullList
        coEvery {
            getSongsUsecase(200)
        } returns Result.Success(loadMoreList)
        coEvery {
            filterSongsUsecase(loadMoreList, any())
        } returns loadMoreList
        coEvery {
            sortSongsUsecase(loadMoreList, any())
        } returns loadMoreList

        viewModel.uiState.test {
            this.consumeInitialEmit()
            viewModel.onLoadMore()
            val result1UIChange = awaitItem()
            assertEquals(200, result1UIChange.limit)
            assertEquals(loadMoreList, result1UIChange.songList)
//            val result1ActualLoadMore = awaitItem()
//            assertEquals(loadMoreList, result1ActualLoadMore.songList)
        }
    }
}