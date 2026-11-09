package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themePreferencesInteractor: ThemePreferenceInteractor,
) : ViewModel() {
    // Основной код
}