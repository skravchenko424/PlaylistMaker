package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.search.network.ITunesAPIService
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.utils.TimeFormatter
import com.example.playlistmaker.utils.impl.TimeFormatterImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesAPIService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesAPIService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("playlist_maker_settings", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<TimeFormatter> {
        TimeFormatterImpl()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }
}