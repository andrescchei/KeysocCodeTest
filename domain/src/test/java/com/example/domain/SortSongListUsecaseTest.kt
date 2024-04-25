package com.example.domain

import com.example.domain.model.SongSortingColumn
import com.example.domain.usecase.ISortSongsUsecase
import com.example.domain.usecase.SortSongsUsecaseImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SortSongListUsecaseTest {

    private lateinit var sortSongsUsecase: ISortSongsUsecase

    val fullList = listOf(
        SongModelUtil.createSong("B", "B"),
        SongModelUtil.createSong("A", "C"),
        SongModelUtil.createSong("C", "A")
    )

    @Before
    fun setup() {
        sortSongsUsecase = SortSongsUsecaseImpl()
    }

    @Test
    fun `test sort song name`() = runTest {
        val sortedList = listOf(
            SongModelUtil.createSong("A", "C"),
            SongModelUtil.createSong("B", "B"),
            SongModelUtil.createSong("C", "A")
        )

        val result = sortSongsUsecase.invoke(fullList, SongSortingColumn.SONG_NAME)
        assertEquals(
            sortedList,
            result
        )
    }

    @Test
    fun `test sort album name`() = runTest {
        val sortedList = listOf(
            SongModelUtil.createSong("C", "A"),
            SongModelUtil.createSong("B", "B"),
            SongModelUtil.createSong("A", "C")
        )

        val result = sortSongsUsecase.invoke(fullList, SongSortingColumn.ALBUM_NAME)
        assertEquals(
            sortedList,
            result
        )
    }
}