package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryRepository {
    fun getTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
    fun clear()
}