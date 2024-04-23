package com.example.data.di

import com.example.data.repository.ItunesMusicListRepositoryImpl
import com.example.data.repository.IItunesMusicListRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::ItunesMusicListRepositoryImpl) bind IItunesMusicListRepository::class
}