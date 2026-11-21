package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.TrackRepository
import java.util.concurrent.ExecutorService

class TrackInteractorImpl(
    private val repository: TrackRepository,
    private val executor: ExecutorService
    ) : TrackInteractor {
    override fun searchTracks(searchText: String, entity: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(searchText, entity))
        }
    }
}