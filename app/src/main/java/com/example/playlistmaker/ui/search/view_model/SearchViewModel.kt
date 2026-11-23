package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.model.TrackSearchState

class SearchViewModel(
    private val searchHistory: SearchHistoryInteractor,
    private val trackInteractor: TrackInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<TrackSearchState>()
    fun observeState(): LiveData<TrackSearchState> = stateLiveData

    private val searchQueryLiveData = MutableLiveData("")
    fun observeSearchQuery(): LiveData<String> = searchQueryLiveData

    private val handler = Handler(Looper.getMainLooper())

    fun onSearchQueryChanged(query: String) {
        searchQueryLiveData.value = query
    }

    private var currentQuery: String? = null

    fun searchRequest(searchText: String) {
        if (searchText.isEmpty()) {
            currentQuery = null
            renderState(TrackSearchState.SearchHistory(tracks = searchHistory.getTracks()))
            return
        }

        currentQuery = searchText
        renderState(TrackSearchState.Loading)

        trackInteractor.searchTracks(searchText, "song", object : TrackInteractor.TrackConsumer {
            override fun consume(result: TrackSearchState) {
                handler.post {
                    if (searchText == currentQuery) {
                        renderState(result)
                    }
                }
            }
        })
    }

    fun searchDebounce(searchText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(searchText) }
        handler.postDelayed(searchRunnable, SEARCH_REQUEST_TOKEN, SEARCH_DEBOUNCE_DELAY)
    }

    fun retrieveSearchHistory() {
        renderState(TrackSearchState.SearchHistory(tracks = searchHistory.getTracks()))
    }

    fun clearSearchHistory(){
        searchHistory.clearHistory()
        renderState(TrackSearchState.SearchHistory(tracks = emptyList()))
    }

    fun addTrackToHistory(track: Track){
        searchHistory.addTrack(track)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun renderState(state: TrackSearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}