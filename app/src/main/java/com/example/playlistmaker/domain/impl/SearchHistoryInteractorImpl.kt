package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun addTrack(track: Track) {
        val current = repository.getTracks().toMutableList()

        // Remove the track if it already exists (based on trackId)
        current.removeAll { it.trackId == track.trackId }

        current.add(0, track)

        if (current.size > MAX_HISTORY_SIZE) {
            current.subList(MAX_HISTORY_SIZE, current.size).clear()
        }

        repository.saveTracks(current)
    }

    override fun getTracks(): List<Track> = repository.getTracks()

    override fun clearHistory() = repository.clear()

    override fun isEmpty(): Boolean = repository.getTracks().isEmpty()

    override fun size(): Int = repository.getTracks().size

    companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}