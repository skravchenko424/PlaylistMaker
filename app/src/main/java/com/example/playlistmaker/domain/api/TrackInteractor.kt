package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchResult

interface TrackInteractor {
    fun searchTracks(searchText: String, entity: String = "song", consumer: TrackInteractor.TrackConsumer)

    interface TrackConsumer {
        fun consume(result: SearchResult)
    }
}