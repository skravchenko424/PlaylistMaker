package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {
    fun shareApp(link: String)
    fun openTerms(link: String)
    fun openSupport(emailData: EmailData)
}