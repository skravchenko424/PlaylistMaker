package com.example.playlistmaker.domain.settings

interface ThemePreferenceInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}