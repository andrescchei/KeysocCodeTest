package com.example.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val coroutineModule = module {
    single {
        CoroutineDispatcher(
            Dispatchers.Main,
            Dispatchers.IO,
            Dispatchers.Default
        )
    }
}

data class CoroutineDispatcher(
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val default: CoroutineDispatcher,
)