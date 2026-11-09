package com.example.playlistmaker.domain.utils

interface TimeFormatter {
    fun format(millis: Long): String
}