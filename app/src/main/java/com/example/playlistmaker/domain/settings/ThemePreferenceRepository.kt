package com.example.playlistmaker.domain.settings

interface ThemePreferenceRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}