package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    val gson: Gson
) : SearchHistoryRepository {

    override fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    override fun saveTracks(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply()
    }

    override fun clear() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
    }

    private companion object {
        const val KEY_HISTORY = "search_history"
    }
}