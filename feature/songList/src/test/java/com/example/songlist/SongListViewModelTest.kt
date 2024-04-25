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
        assertEquals(persistentListOf<Song>(), awaitItem().songList)
    }
    @Test
    fun `get full song list success`() = runTest {
        viewModel.uiState.test {
            this.consumeInitialEmit()
            val result = awaitItem().songList
            assertEquals(fullList, result)
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
            val resultKeywordChanged = awaitItem()
            assertEquals("A", resultKeywordChanged.searchKeyword)
            assertEquals(filteredListA, resultKeywordChanged.songList)
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
            val resultKeywordChanged = awaitItem()
            assertEquals(keywordA, resultKeywordChanged.searchKeyword)
            assertEquals(filteredListA, resultKeywordChanged.songList)

            viewModel.onSearch(keywordB)
            val resultKeywordChanged2 = awaitItem()
            assertEquals(keywordB, resultKeywordChanged2.searchKeyword)
            assertEquals(filteredListB, resultKeywordChanged2.songList)

            viewModel.onSearch(keywordA)
            val resultKeywordChanged3 = awaitItem()
            assertEquals(keywordA, resultKeywordChanged3.searchKeyword)
            assertEquals(filteredListA, resultKeywordChanged3.songList)
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
            assertEquals(sortedAlbumList, resultSortChanged.songList)

            viewModel.onSelectSorting(SongSortingColumn.SONG_NAME)
            val resultSortChanged2 = awaitItem()
            assertEquals(SongSortingColumn.SONG_NAME, resultSortChanged2.sortingColumn)
            assertEquals(sortedSongList, resultSortChanged2.songList)

            viewModel.onSelectSorting(SongSortingColumn.ALBUM_NAME)
            val resultSortChanged3 = awaitItem()
            assertEquals(SongSortingColumn.ALBUM_NAME, resultSortChanged3.sortingColumn)
            assertEquals(sortedAlbumList, resultSortChanged3.songList)
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
            val resultInitial = awaitItem()
            assertEquals(100, resultInitial.limit)
            assertEquals(fullList, resultInitial.songList)
            viewModel.onLoadMore()
            val resultLoadMore = awaitItem()
            assertEquals(200, resultLoadMore.limit)
            assertEquals(loadMoreList, resultLoadMore.songList)
        }
    }
}