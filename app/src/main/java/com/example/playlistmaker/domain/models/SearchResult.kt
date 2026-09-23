package com.example.playlistmaker.domain.models

sealed interface SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult
    data class NetworkError(val message: String) : SearchResult
}