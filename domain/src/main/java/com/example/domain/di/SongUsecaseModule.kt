package com.example.domain.di

import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.GetSongsUsecaseImpl
import com.example.domain.usecase.IFilterSongsUsecase
import com.example.domain.usecase.FilterSongsUsecaseImpl
import com.example.domain.usecase.ISortSongsUsecase
import com.example.domain.usecase.SortSongsUsecaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.dsl.bind

val songUsecaseModule = module {
    factoryOf(::GetSongsUsecaseImpl) bind IGetSongsUsecase::class
    factoryOf(::SortSongsUsecaseImpl) bind ISortSongsUsecase::class
    factoryOf(::FilterSongsUsecaseImpl) bind IFilterSongsUsecase::class
}