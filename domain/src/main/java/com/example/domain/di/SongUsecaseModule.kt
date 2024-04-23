package com.example.domain.di

import com.example.domain.usecase.IGetSongsUsecase
import com.example.domain.usecase.GetSongsUsecaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.dsl.bind

val songUsecaseModule = module {
    factoryOf(::GetSongsUsecaseImpl) bind IGetSongsUsecase::class
}