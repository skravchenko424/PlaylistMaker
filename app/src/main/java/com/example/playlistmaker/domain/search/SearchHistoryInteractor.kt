package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getTracks(): List<Track>
    fun clearHistory()
    fun isEmpty(): Boolean
    fun size(): Int
}