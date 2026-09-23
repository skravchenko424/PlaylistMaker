package com.example.playlistmaker.domain.api

interface TimeFormatter {
    fun format(millis: Long): String
}