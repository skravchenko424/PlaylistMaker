package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val KEY_HISTORY = "search_history"
    private val MAX_HISTORY_SIZE = 10

    fun addTrack(track: Track) {
        val currentHistory = getTracks().toMutableList()

        // Remove the track if it already exists (based on trackId)
        currentHistory.removeAll { it.trackId == track.trackId }

        // Add the track to the beginning of the list
        currentHistory.add(0, track)

        // Trim the list if it exceeds max size
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        // Save the updated list
        saveTracks(currentHistory)
    }

    fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveTracks(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply()
    }

    fun isEmpty(): Boolean {
        return getTracks().isEmpty()
    }

    fun size(): Int {
        return getTracks().size
    }
}