package com.example.songlist

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.example.common.CoroutineDispatcher
import com.example.domain.model.Result
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
            val resultKeywordChanged = awaitItem().searchKeyword
            assertEquals("A", resultKeywordChanged)
            val resultListChanged = awaitItem().songList
            assertEquals(filteredListA, resultListChanged)
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
            val resultKeywordChanged = awaitItem().searchKeyword
            assertEquals(keywordA, resultKeywordChanged)
            val resultListChanged = awaitItem().songList
            assertEquals(filteredListA, resultListChanged)

            viewModel.onSearch(keywordB)
            val resultKeywordChanged2 = awaitItem().searchKeyword
            assertEquals(keywordB, resultKeywordChanged2)
            val resultListChanged2 = awaitItem().songList
            assertEquals(filteredListB, resultListChanged2)

            viewModel.onSearch(keywordA)
            val resultKeywordChanged3 = awaitItem().searchKeyword
            assertEquals(keywordA, resultKeywordChanged3)
            val resultListChanged3 = awaitItem().songList
            assertEquals(filteredListA, resultListChanged3)
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
            val resultSortChanged = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, resultSortChanged.sortingColumn)
            val resultSortListChanged = awaitItem()
            assertEquals(sortedAlbumList, resultSortListChanged.songList)

            viewModel.onSelectSorting(SongSortingColumn.SONG_NAME)
            val resultSortChanged2 = awaitItem()
            assertEquals(SongSortingColumn.SONG_NAME, resultSortChanged2.sortingColumn)
            val resultSortListChanged2 = awaitItem()
            assertEquals(sortedSongList, resultSortListChanged2.songList)

            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val resultSortChanged3 = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, resultSortChanged3.sortingColumn)
            val resultSortListChanged3 = awaitItem()
            assertEquals(sortedAlbumList, resultSortListChanged3.songList)
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
            val resultLimitChanged = awaitItem()
            assertEquals(200, resultLimitChanged.limit)
            val resultListChanged = awaitItem()
            assertEquals(loadMoreList, resultListChanged.songList)
        }
    }
}