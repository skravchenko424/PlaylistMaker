package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themePreferencesInteractor: ThemePreferenceInteractor
) : ViewModel() {
    private val isDarkThemeLiveData = MutableLiveData<Boolean>()
    fun observeIsDarkTheme(): LiveData<Boolean> = isDarkThemeLiveData

    fun switchTheme(isDarkTheme: Boolean) {
        themePreferencesInteractor.setDarkTheme(isDarkTheme)
        isDarkThemeLiveData.postValue(isDarkTheme)
    }

    fun shareApp(link: String) {
        sharingInteractor.shareApp(link)
    }

    fun openTerms(link: String) {
        sharingInteractor.openTerms(link)
    }

    fun openSupport(emailData: EmailData) {
        sharingInteractor.openSupport(emailData)
    }

    companion object {
        fun getFactory(sharingInteractor: SharingInteractor, themePreferencesInteractor: ThemePreferenceInteractor):
                ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, themePreferencesInteractor)
            }
        }
    }
}