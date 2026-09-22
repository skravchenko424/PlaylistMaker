package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.ThemePreferenceRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemePreferenceInteractor
import com.example.playlistmaker.domain.api.ThemePreferenceRepository
import com.example.playlistmaker.domain.api.TimeFormatter
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemePreferenceInteractorImpl
import com.example.playlistmaker.domain.impl.TimeFormatterImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), provideTimeFormatter())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    fun provideTimeFormatter(): TimeFormatter {
        return TimeFormatterImpl()
    }

    private fun getThemeRepository(context: Context): ThemePreferenceRepository {
        return ThemePreferenceRepositoryImpl(context)
    }

    fun provideThemeInteractor(context: Context): ThemePreferenceInteractor {
        return ThemePreferenceInteractorImpl(getThemeRepository(context))
    }
}