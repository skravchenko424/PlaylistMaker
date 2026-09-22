package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
    fun clear()
}