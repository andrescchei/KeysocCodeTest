package com.example.songlist.di

import com.example.songlist.viewmodel.SongListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val songListModule = module {
    viewModelOf(::SongListViewModel)
}