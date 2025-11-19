package com.aradsheybak.pos_simulator_iso8583.di

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.ISO8583Config
import com.aradsheybak.pos_simulator_iso8583.core.domain.repository.TransactionRepository
import com.aradsheybak.pos_simulator_iso8583.data.datasource.remote.ISOSocketDataSource
import com.aradsheybak.pos_simulator_iso8583.data.mapper.TransactionMapper
import com.aradsheybak.pos_simulator_iso8583.data.repository.TransactionRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {

    // DataSources
    singleOf(::ISOSocketDataSource)

    // Mappers
    singleOf(::TransactionMapper)

    // Config
    single {
        ISO8583Config(
            serverHost = "192.168.x.xxx",  // Change to your server IP
            serverPort = 5000,
            timeout = 10000
        )
    }

    // Repository
    single<TransactionRepository> {
        TransactionRepositoryImpl(
            socketDataSource = get(),
            transactionMapper = get(),
            config = get()
        )
    }
}