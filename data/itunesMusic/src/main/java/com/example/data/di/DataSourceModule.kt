package com.example.data.di

import com.example.data.datasource.IItunesMusicListDatasource
import com.example.data.datasource.ItunesMusicListDatasourceImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule = module {
    factoryOf(::ItunesMusicListDatasourceImpl) bind IItunesMusicListDatasource::class
}