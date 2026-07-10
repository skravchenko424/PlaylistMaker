package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TracksAdapter
    private val trackList = mutableListOf<Track>()
    private lateinit var itunesService: ITunesAPIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.back_from_search_button).setOnClickListener {
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.etSearch)
        val clearButton = findViewById<ImageView>(R.id.ivClear)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            // ask input edit to hide the keyboard
            inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = clearButtonVisibility(text)
            searchText = text ?: ""
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.search_reload_button).setOnClickListener {
            performSearch()
        }

        // setup track list view
        val trackListView = findViewById<RecyclerView>(R.id.track_list_view)
        trackListView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TracksAdapter(trackList)
        trackListView.adapter = trackAdapter

        val itunesBaseUrl = getString(R.string.url_itunes)
        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        itunesService = retrofit.create(ITunesAPIService::class.java)

        // Search on Enter key
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            }
            false
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
        // Clear previous results and show loading state
        trackList.clear()
        trackAdapter.notifyDataSetChanged()

        hidePlaceHolder()

        val inputEditText = findViewById<EditText>(R.id.etSearch)
        val searchText = inputEditText.text.toString().trim()

        if ( searchText.isEmpty() ) {
            return
        }

        itunesService.findSong(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.code() == 200) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                        trackList.clear()
                        trackList.addAll(searchResponse.results)
                        trackAdapter.notifyDataSetChanged()
                    } else {
                        // No results found
                        trackList.clear()
                        trackAdapter.notifyDataSetChanged()
                        showNothingFound()
                    }
                } else {
                    // Error response
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    showNetworkError()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Network failure
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                showNetworkError()
            }
        })
    }

    private fun showNothingFound() {
        val placeHolder = findViewById<ImageView>(R.id.ivSearchErrorPlaceholder)
        placeHolder.visibility = View.VISIBLE
        placeHolder.setImageResource(R.drawable.ic_nothing_found_placeholder_120)

        val errorText = findViewById<TextView>(R.id.tvSearchErrorText)
        errorText.setText(getString(R.string.error_nothing_found))
        errorText.visibility = View.VISIBLE

        val reloadButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.search_reload_button)
        reloadButton.visibility = View.GONE
    }

    private fun showNetworkError() {
        val placeHolder = findViewById<ImageView>(R.id.ivSearchErrorPlaceholder)
        placeHolder.visibility = View.VISIBLE
        placeHolder.setImageResource(R.drawable.ic_network_error_placeholder_120)

        val errorText = findViewById<TextView>(R.id.tvSearchErrorText)
        errorText.setText(getString(R.string.error_network_failure))
        errorText.visibility = View.VISIBLE

        val reloadButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.search_reload_button)
        reloadButton.visibility = View.VISIBLE
    }

    private fun hidePlaceHolder() {
        val placeHolder = findViewById<ImageView>(R.id.ivSearchErrorPlaceholder)
        placeHolder.visibility = View.GONE

        val errorText = findViewById<TextView>(R.id.tvSearchErrorText)
        errorText.visibility = View.GONE

        val reloadButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.search_reload_button)
        reloadButton.visibility = View.GONE
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
    }
}