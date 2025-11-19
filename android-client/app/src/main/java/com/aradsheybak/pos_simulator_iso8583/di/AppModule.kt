package com.aradsheybak.pos_simulator_iso8583.di

import org.koin.dsl.module

val appModule = module {

    // Combine all modules
        includes(dataModule, domainModule, viewModelModule)
    }
