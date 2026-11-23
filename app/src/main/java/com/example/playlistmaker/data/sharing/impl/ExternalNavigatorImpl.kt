package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator{
    private fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun shareLink(link: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_title)))
    }

    override fun openLink(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, link.toUri()))
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailData.mailTo.toUri()
            putExtra(Intent.EXTRA_EMAIL, emailData.recipients)
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        startActivity(supportIntent)
    }
}