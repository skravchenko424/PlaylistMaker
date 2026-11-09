package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.SearchResult

interface TrackInteractor {
    fun searchTracks(searchText: String, entity: String = "song", consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(result: SearchResult)
    }
}