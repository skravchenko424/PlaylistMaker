package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.SearchResult

interface TrackRepository {
    fun searchTracks(searchText: String, entity: String = "song"): SearchResult
}