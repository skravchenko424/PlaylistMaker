package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest

class RetrofitNetworkClient(
    private val itunesService: ITunesAPIService
)  : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return try {
            val resp = itunesService.searchTracks(dto.searchText).execute()
            val body = resp.body() ?: Response()
            body.apply { resultCode = resp.code() }
        } catch (e: Exception) {
            // network failure — sentinel code so repository can map it
            Response().apply { resultCode = -1 }
        }
    }
}