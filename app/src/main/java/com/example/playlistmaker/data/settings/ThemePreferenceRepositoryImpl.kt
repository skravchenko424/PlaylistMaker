package com.example.playlistmaker.data.settings

import android.content.Context
import com.example.playlistmaker.domain.api.ThemePreferenceRepository

class ThemePreferenceRepositoryImpl(context: Context) : ThemePreferenceRepository {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean =
        prefs.getBoolean(KEY_DARK_THEME, DEFAULT_DARK_THEME)

    override fun setDarkTheme(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "playlist_maker_settings"
        const val KEY_DARK_THEME = "dark_theme"
        const val DEFAULT_DARK_THEME = false
    }
}