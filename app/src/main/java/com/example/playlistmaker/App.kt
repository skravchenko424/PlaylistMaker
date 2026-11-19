package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor

class App : Application() {

    private lateinit var themePreferenceInteractor: ThemePreferenceInteractor

    override fun onCreate() {
        super.onCreate()

        themePreferenceInteractor = Creator.provideThemeInteractor(this)
        applyTheme(themePreferenceInteractor.isDarkTheme())
    }

    fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}