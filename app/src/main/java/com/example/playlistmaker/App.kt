package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemePreferenceInteractor

const val PLAYLISTMAKER_EXAMPLE_PREFERENCES = "playlistmaker_example_preferences"
const val DARK_THEME_KEY = "dark_theme"

class App : Application() {

    private lateinit var themePreferenceInteractor: ThemePreferenceInteractor

    override fun onCreate() {
        super.onCreate()

        themePreferenceInteractor = Creator.provideThemeInteractor(this)
        applyTheme(themePreferenceInteractor.isDarkTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        themePreferenceInteractor.setDarkTheme(darkThemeEnabled)
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}