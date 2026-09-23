package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TimeFormatter
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val timeFormatter: TimeFormatter
) : TrackRepository {

    override fun searchTracks(searchText: String, entity: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(searchText, entity))

        return when (response.resultCode) {
            200 -> {
                val tracks = (response as TrackSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTime = timeFormatter.format(it.trackTimeMillis),
                        artworkUrl100 = it.artworkUrl100,
                        artworkUrl512 = it.artworkUrl100.replace("100x100", "512x512"),
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl
                    )
                }
                SearchResult.Success(tracks)
            }
            else -> SearchResult.NetworkError("HTTP ${response.resultCode}")
        }
    }

}