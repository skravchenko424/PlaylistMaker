package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TimeFormatter
import java.util.Locale

class TimeFormatterImpl : TimeFormatter {

    override fun format(millis: Long): String {
        if (millis <= 0) return "0:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}