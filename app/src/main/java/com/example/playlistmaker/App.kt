package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_EXAMPLE_PREFERENCES = "playlistmaker_example_preferences"
const val DARK_THEME_KEY = "dark_theme"

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        // Достаём сохранённые настройки из SharedPreferences
        val sharedPref = getSharedPreferences(PLAYLISTMAKER_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(DARK_THEME_KEY, false)

        // Применяем тему при запуске
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        applyTheme(darkThemeEnabled)

        // Сохраняем настройку в SharedPreferences
        saveThemePreference(darkThemeEnabled)
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun saveThemePreference(isDark: Boolean) {
        val sharedPref = getSharedPreferences(PLAYLISTMAKER_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        sharedPref.edit().putBoolean(DARK_THEME_KEY, isDark).apply()
    }
}