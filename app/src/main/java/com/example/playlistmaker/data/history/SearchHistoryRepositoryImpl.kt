package com.example.playlistmaker.data.history

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {

    private companion object {
        const val PREFS_NAME = "playlist_maker_prefs"
        const val KEY_HISTORY = "search_history"
    }

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

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
}