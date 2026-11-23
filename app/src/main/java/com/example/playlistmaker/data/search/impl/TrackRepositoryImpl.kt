package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.utils.TimeFormatter
import com.example.playlistmaker.domain.search.TrackRepository
import com.example.playlistmaker.domain.search.model.TrackSearchState
import com.example.playlistmaker.domain.search.model.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val timeFormatter: TimeFormatter
) : TrackRepository {

    override fun searchTracks(searchText: String, entity: String): TrackSearchState {
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
                TrackSearchState.Success(tracks)
            }
            else -> TrackSearchState.NetworkError("HTTP ${response.resultCode}")
        }
    }

}