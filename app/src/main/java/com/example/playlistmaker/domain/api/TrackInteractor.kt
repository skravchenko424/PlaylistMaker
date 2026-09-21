package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun searchTracks(searchText: String, entity: String = "song", consumer: TrackInteractor.TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }
}