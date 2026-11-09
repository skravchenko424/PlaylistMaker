package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.ThemePreferenceInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var themePreferenceInteractor: ThemePreferenceInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themePreferenceInteractor = Creator.provideThemeInteractor(this)
        sharingInteractor = Creator.provideSharingInteractor(this)

        findViewById<ImageView>(R.id.back_from_settings_button).setOnClickListener {
            finish()
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeSwitcher.isChecked = themePreferenceInteractor.isDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        findViewById<ImageView>(R.id.share_button).setOnClickListener {
            sharingInteractor.shareApp(getString(R.string.share_link))
        }

        findViewById<ImageView>(R.id.contact_support_button).setOnClickListener {
            val emailData = EmailData(
                mailTo = "mailto:",
                recipients = arrayOf(getString(R.string.support_mail)),
                subject = getString(R.string.support_subject),
                body = getString(R.string.support_text_body)
            )

            sharingInteractor.openSupport(emailData)
        }

        findViewById<ImageView>(R.id.license_agreement_button).setOnClickListener {
            sharingInteractor.openTerms(getString(R.string.license_agreement_link))
        }
    }
}