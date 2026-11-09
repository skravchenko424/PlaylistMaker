package com.example.playlistmaker.data.search.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDTO>
) : Response()