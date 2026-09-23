package com.example.playlistmaker.domain.api

interface ThemePreferenceRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}