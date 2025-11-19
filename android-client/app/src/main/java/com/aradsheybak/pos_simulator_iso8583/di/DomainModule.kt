package com.aradsheybak.pos_simulator_iso8583.di

import com.aradsheybak.pos_simulator_iso8583.core.domain.usecase.SendTransactionUseCase
import com.aradsheybak.pos_simulator_iso8583.core.domain.usecase.ValidateTransactionUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {

    // UseCases
    singleOf(::SendTransactionUseCase)
    singleOf(::ValidateTransactionUseCase)
}