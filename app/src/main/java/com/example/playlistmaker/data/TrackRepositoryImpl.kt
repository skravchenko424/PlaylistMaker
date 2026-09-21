package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(searchText: String, entity: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(searchText, entity))

        return when (response.resultCode) {
            200 -> {
                val tracks = (response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                SearchResult.Success(tracks)
            }
            else -> SearchResult.NetworkError("HTTP ${response.resultCode}")
        }
    }
}