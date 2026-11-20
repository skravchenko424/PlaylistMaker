package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(
            Creator.provideSharingInteractor(this),
            Creator.provideThemeInteractor(this)))
            .get(SettingsViewModel::class.java)

        viewModel.observeIsDarkTheme().observe(this) {
            themeSwitcher.isChecked = it
            (applicationContext as App).applyTheme(it)
        }

        findViewById<ImageView>(R.id.back_from_settings_button).setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        findViewById<ImageView>(R.id.share_button).setOnClickListener {
            viewModel.shareApp(getString(R.string.share_link))
        }

        findViewById<ImageView>(R.id.contact_support_button).setOnClickListener {
            val emailData = EmailData(
                mailTo = "mailto:",
                recipients = arrayOf(getString(R.string.support_mail)),
                subject = getString(R.string.support_subject),
                body = getString(R.string.support_text_body)
            )

            viewModel.openSupport(emailData)
        }

        findViewById<ImageView>(R.id.license_agreement_button).setOnClickListener {
            viewModel.openTerms(getString(R.string.license_agreement_link))
        }
    }
}