package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.TrackSearchState

interface TrackRepository {
    fun searchTracks(searchText: String, entity: String = "song"): TrackSearchState
}