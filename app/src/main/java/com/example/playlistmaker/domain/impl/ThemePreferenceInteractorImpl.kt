package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemePreferenceInteractor
import com.example.playlistmaker.domain.api.ThemePreferenceRepository

class ThemePreferenceInteractorImpl(
    private val repository: ThemePreferenceRepository
) : ThemePreferenceInteractor {

    override fun isDarkTheme(): Boolean = repository.isDarkTheme()

    override fun setDarkTheme(enabled: Boolean) = repository.setDarkTheme(enabled)
}