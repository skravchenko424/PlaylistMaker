package com.example.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.search.model.TrackSearchState
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var trackAdapter: TracksAdapter
    private val trackList = mutableListOf<Track>()

    private lateinit var placeHolder: ImageView
    private lateinit var errorText: TextView
    private lateinit var reloadButton: MaterialButton
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView
    private lateinit var searchHistoryHeaderText: TextView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar

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

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory(
            Creator.provideSearchHistoryInteractor(this),
            Creator.provideTrackInteractor()))
            .get(SearchViewModel::class.java)

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeSearchQuery().observe(this) { query ->
            if (inputEditText.text.toString() != query) {
                inputEditText.setText(query)
                inputEditText.setSelection(query.length)
            }
        }

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
                viewModel.retrieveSearchHistory()
            }
            else {
                hideAll()
            }
        }

        inputEditText.doOnTextChanged { text, start, before, count ->
            clearButton.visibility = clearButtonVisibility(text)
            viewModel.onSearchQueryChanged(text?.toString() ?: "")

            if (inputEditText.hasFocus() && text?.isEmpty() == true)  {
                viewModel.retrieveSearchHistory()
            }
            else {
                hideAll()
            }

            val searchText = inputEditText.text.toString().trim()
            viewModel.searchDebounce(searchText)
        }

        findViewById<MaterialButton>(R.id.search_reload_button).setOnClickListener {
            val searchText = inputEditText.text.toString().trim()
            viewModel.searchDebounce(searchText)
        }

        trackListView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TracksAdapter(trackList) { track ->
            handleTrackClick(track)
        }
        trackListView.adapter = trackAdapter

        // Search on Enter key
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = inputEditText.text.toString().trim()
                viewModel.searchDebounce(searchText)
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

    private fun handleTrackClick(track: Track) {
        if ( clickDebounce()) {
            viewModel.addTrackToHistory(track)
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

    private fun clearSearchHistory() {
        viewModel.clearSearchHistory()
    }

    private fun hideAll() {
        // placeholders
        placeHolder.visibility = View.GONE
        errorText.visibility = View.GONE
        reloadButton.visibility = View.GONE

        // search history
        searchHistoryHeaderText.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE

        // progressbar
        progressBar.visibility = View.GONE

        // track list
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun renderSearchHistory(historyTracks: List<Track>) {
        hideAll()

        trackList.clear()
        trackList.addAll(historyTracks)
        trackAdapter.notifyDataSetChanged()

        if (historyTracks.isNotEmpty()) {
            searchHistoryHeaderText.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun renderLoading() {
        hideAll()

        progressBar.visibility = View.VISIBLE
    }

    private fun renderFoundTracks(tracks: List<Track>) {
        hideAll()

        if ( tracks.isEmpty() ){
            placeHolder.visibility = View.VISIBLE
            placeHolder.setImageResource(R.drawable.ic_nothing_found_placeholder_120)

            errorText.setText(getString(R.string.error_nothing_found))
            errorText.visibility = View.VISIBLE
        }
        else {
            trackList.clear()
            trackList.addAll(tracks)
            trackAdapter.notifyDataSetChanged()
        }
    }

    private fun renderNetworkError(errorMessage: String){
        Log.e("NetworkError", "Ошибка сети: $errorMessage")

        hideAll()

        placeHolder.visibility = View.VISIBLE
        placeHolder.setImageResource(R.drawable.ic_network_error_placeholder_120)

        errorText.setText(getString(R.string.error_network_failure))
        errorText.visibility = View.VISIBLE

        reloadButton.visibility = View.VISIBLE
    }

    fun render(state: TrackSearchState) {
        when (state) {
            is TrackSearchState.SearchHistory -> renderSearchHistory(state.tracks)
            is TrackSearchState.Loading -> renderLoading()
            is TrackSearchState.Success -> renderFoundTracks(state.tracks)
            is TrackSearchState.NetworkError -> renderNetworkError(state.message)
        }
    }

    companion object {
        private const val ITEM_CLICK_DEBOUNCE_DELAY = 1000L
    }
}