package com.example.domain

import com.example.domain.usecase.FilterSongsUsecaseImpl
import com.example.domain.usecase.IFilterSongsUsecase
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FilterSongListUsecaseTest {

    private lateinit var filterSongsUsecase: IFilterSongsUsecase

    val fullList = listOf(
        SongModelUtil.createSong("Talking to the Moon", albumName = "Doo-Wops & Hooligans"),
        SongModelUtil.createSong("Just the Way You Are", albumName = "Doo-Wops & Hooligans"),
        SongModelUtil.createSong("Yellow", albumName = "Parachutes")
    )

    @Before
    fun setup() {
        filterSongsUsecase = FilterSongsUsecaseImpl()
    }
    @Test
    fun `test filter song name with keyword`() = runTest {
        val keyword = "the"
        val filteredList = listOf(
            SongModelUtil.createSong("Talking to the Moon", albumName = "Doo-Wops & Hooligans"),
            SongModelUtil.createSong("Just the Way You Are", albumName = "Doo-Wops & Hooligans"),
        )

        val result = filterSongsUsecase.invoke(fullList, keyword)
        assertEquals(
            filteredList,
            result
        )
    }

    @Test
    fun `test filter album name with keyword`() = runTest {
        val keyword = "Para"
        val filteredList = listOf(
            SongModelUtil.createSong("Yellow", albumName = "Parachutes")
        )
        val result = filterSongsUsecase.invoke(fullList, keyword)
        assertEquals(
            filteredList,
            result
        )
    }
}