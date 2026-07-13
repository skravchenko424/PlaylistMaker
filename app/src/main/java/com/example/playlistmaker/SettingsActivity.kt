package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

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

        findViewById<ImageView>(R.id.back_from_settings_button).setOnClickListener {
            finish()
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)

        // Устанавливаем состояние переключателя в соответствии с текущей темой
        val app = applicationContext as App
        themeSwitcher.isChecked = app.darkTheme

        // Устанавливаем слушатель для переключения темы
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        findViewById<ImageView>(R.id.share_button).setOnClickListener {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            sendIntent.setType("text/plain")

            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_title))
            startActivity(shareIntent)
        }

        findViewById<ImageView>(R.id.contact_support_button).setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = "mailto:".toUri()
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mail)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text_body))

            startActivity(supportIntent)
        }


        findViewById<ImageView>(R.id.license_agreement_button).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                getString(R.string.license_agreement_link).toUri())
            startActivity(browserIntent)
        }
    }
}