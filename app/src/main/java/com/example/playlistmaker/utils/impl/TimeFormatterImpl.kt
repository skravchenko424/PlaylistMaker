package com.example.playlistmaker.utils.impl

import com.example.playlistmaker.utils.TimeFormatter
import java.util.Locale

class TimeFormatterImpl : TimeFormatter {

    override fun format(millis: Long): String {
        if (millis <= 0) return "0:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.Companion.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}