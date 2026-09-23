package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesAPIService::class.java)

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