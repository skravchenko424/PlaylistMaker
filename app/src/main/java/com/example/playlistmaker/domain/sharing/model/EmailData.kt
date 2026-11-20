package com.example.playlistmaker.domain.sharing.model

data class EmailData(
    val mailTo: String,
    val recipients: Array<String>,
    val subject: String,
    val body: String
)


