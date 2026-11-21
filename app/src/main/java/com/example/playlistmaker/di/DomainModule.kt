package com.example.playlistmaker.di

import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.ThemePreferenceRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor
import com.example.playlistmaker.domain.settings.ThemePreferenceRepository
import com.example.playlistmaker.domain.settings.impl.ThemePreferenceInteractorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val domainModule = module {

    single<ExecutorService> {
        Executors.newCachedThreadPool()
    }
    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }
    single<TrackInteractor> {
        TrackInteractorImpl(get(), get())
    }
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }
    single<ThemePreferenceRepository> {
        ThemePreferenceRepositoryImpl(get())
    }
    single<ThemePreferenceInteractor> {
        ThemePreferenceInteractorImpl(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}