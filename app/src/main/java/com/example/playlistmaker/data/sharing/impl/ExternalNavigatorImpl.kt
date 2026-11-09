package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator{
    override fun shareLink(link: String) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, link)
        sendIntent.setType("text/plain")

        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_title))
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            link.toUri()
        )
        context.startActivity(browserIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = emailData.mailTo.toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, emailData.recipients)
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.body)

        context.startActivity(supportIntent)
    }
}