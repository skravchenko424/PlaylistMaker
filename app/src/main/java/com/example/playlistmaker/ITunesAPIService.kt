package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPIService {
    @GET("search")
    fun findSong(
        @Query("term") searchText: String,
        @Query("entity") entity: String = "song"
    ): Call<SearchResponse>
}