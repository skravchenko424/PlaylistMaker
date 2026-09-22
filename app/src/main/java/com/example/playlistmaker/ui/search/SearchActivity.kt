package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TracksAdapter
    private val trackList = mutableListOf<Track>()
    private lateinit var searchHistory: SearchHistoryInteractor
    private lateinit var placeHolder: ImageView
    private lateinit var errorText: TextView
    private lateinit var reloadButton: MaterialButton
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView
    private lateinit var searchHistoryHeaderText: TextView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar


    private lateinit var trackInteractor: TrackInteractor

    private var isTrackClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchHistory = Creator.provideSearchHistoryInteractor(this)

        findViewById<ImageView>(R.id.back_from_search_button).setOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.etSearch)
        clearButton = findViewById(R.id.ivClear)
        placeHolder = findViewById(R.id.ivSearchErrorPlaceholder)
        errorText = findViewById(R.id.tvSearchErrorText)
        reloadButton = findViewById(R.id.search_reload_button)
        trackListView = findViewById(R.id.track_list_view)
        searchHistoryHeaderText = findViewById(R.id.tvHistoryHeader)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            // ask input edit to hide the keyboard
            inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showHistory()
            }
            else {
                hideHistory()
            }
        }

        inputEditText.doOnTextChanged { text, start, before, count ->
            clearButton.visibility = clearButtonVisibility(text)
            searchText = text ?: ""

            if (inputEditText.hasFocus() && text?.isEmpty() == true)  {
                showHistory()
            }
            else {
                hideHistory()
            }

            searchDebounce()
        }

        findViewById<MaterialButton>(R.id.search_reload_button).setOnClickListener {
            performSearch()
        }

        // setup track list view
        trackListView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TracksAdapter(trackList) { track ->
            // This lambda will be called when a track is clicked
            handleTrackClick(track)
        }
        trackListView.adapter = trackAdapter

        trackInteractor = Creator.provideTrackInteractor()

        // Search on Enter key
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private var searchText: CharSequence = SEARCH_TEXT_DEF

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getCharSequence(SEARCH_TEXT, SEARCH_TEXT_DEF)
        val inputEditText = findViewById<EditText>(R.id.etSearch)
        inputEditText.setText(searchText)
    }

    private fun performSearch() {
        hideHistory()
        hidePlaceHolder()

        progressBar.visibility = View.VISIBLE

        val searchText = inputEditText.text.toString().trim()

        if ( searchText.isEmpty() ) {
            showHistory()
            return
        }

        trackInteractor.searchTracks(searchText, "song", object : TrackInteractor.TrackConsumer {
            override fun consume(result: SearchResult) {
                handler.post {
                    progressBar.visibility = View.GONE

                    when (result) {
                        is SearchResult.Success -> {
                            trackList.clear()
                            trackList.addAll(result.tracks)
                            trackAdapter.notifyDataSetChanged()
                            if (result.tracks.isEmpty()) showNothingFound()
                        }
                        is SearchResult.NetworkError -> {
                            trackList.clear()
                            trackAdapter.notifyDataSetChanged()
                            showNetworkError()
                        }
                    }
                }
            }
        })
    }

    private val searchRunnable = Runnable { performSearch() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun handleTrackClick(track: Track) {
        if ( clickDebounce()) {
            // Save the clicked track to history
            searchHistory.addTrack(track)
            val displayIntent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(Track.Companion.TRACK_EXTRA_NAME, track)
            }
            startActivity(displayIntent)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, ITEM_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showNothingFound() {
        placeHolder.visibility = View.VISIBLE
        placeHolder.setImageResource(R.drawable.ic_nothing_found_placeholder_120)

        errorText.setText(getString(R.string.error_nothing_found))
        errorText.visibility = View.VISIBLE

        reloadButton.visibility = View.GONE
    }

    private fun showNetworkError() {
        placeHolder.visibility = View.VISIBLE
        placeHolder.setImageResource(R.drawable.ic_network_error_placeholder_120)

        errorText.setText(getString(R.string.error_network_failure))
        errorText.visibility = View.VISIBLE

        reloadButton.visibility = View.VISIBLE
    }

    private fun hidePlaceHolder() {
        placeHolder.visibility = View.GONE
        errorText.visibility = View.GONE
        reloadButton.visibility = View.GONE
    }

    private fun showHistory() {
        val historyTracks = searchHistory.getTracks()
        if (historyTracks.isNotEmpty()) {
            trackList.clear()
            trackList.addAll(historyTracks)
            trackAdapter.notifyDataSetChanged()
            hidePlaceHolder()

            searchHistoryHeaderText.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun hideHistory() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        searchHistoryHeaderText.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun clearSearchHistory() {
        searchHistory.clearHistory()

        trackList.clear()
        trackAdapter.notifyDataSetChanged()

        searchHistoryHeaderText.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
        private const val ITEM_CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}