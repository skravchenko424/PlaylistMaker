package com.example.playlistmaker.domain.api

interface ThemePreferenceInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}