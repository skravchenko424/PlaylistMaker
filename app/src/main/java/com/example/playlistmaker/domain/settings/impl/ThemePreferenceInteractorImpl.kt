package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor
import com.example.playlistmaker.domain.settings.ThemePreferenceRepository

class ThemePreferenceInteractorImpl(
    private val repository: ThemePreferenceRepository
) : ThemePreferenceInteractor {

    override fun isDarkTheme(): Boolean = repository.isDarkTheme()

    override fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)
}