package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getTracks(): List<Track>
    fun clearHistory()
    fun isEmpty(): Boolean
    fun size(): Int
}