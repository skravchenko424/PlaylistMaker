package com.example.playlistmaker.domain.search.model

sealed interface TrackSearchState {
    data class SearchHistory(val tracks: List<Track>)  : TrackSearchState
    object Loading : TrackSearchState
    data class Success(val tracks: List<Track>) : TrackSearchState
    data class NetworkError(val message: String) : TrackSearchState
}