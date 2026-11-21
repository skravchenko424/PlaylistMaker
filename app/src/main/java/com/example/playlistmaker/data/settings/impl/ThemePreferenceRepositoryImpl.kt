package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.settings.ThemePreferenceRepository

class ThemePreferenceRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemePreferenceRepository {

    override fun isDarkTheme(): Boolean =
        sharedPreferences.getBoolean(KEY_DARK_THEME, DEFAULT_DARK_THEME)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }

    private companion object {
        const val KEY_DARK_THEME = "dark_theme"
        const val DEFAULT_DARK_THEME = false
    }
}