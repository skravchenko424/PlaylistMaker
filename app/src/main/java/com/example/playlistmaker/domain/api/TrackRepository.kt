package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchResult

interface TrackRepository {
    fun searchTracks(searchText: String, entity: String = "song"): SearchResult
}