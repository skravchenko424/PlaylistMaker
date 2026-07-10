package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var trackAdapter: TracksAdapter
    private val trackList = mutableListOf<Track>()
    private lateinit var itunesService: ITunesAPIService
    private lateinit var placeHolder: ImageView
    private lateinit var errorText: TextView
    private lateinit var reloadButton: MaterialButton
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackListView: RecyclerView

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

        inputEditText = findViewById(R.id.etSearch)
        clearButton = findViewById(R.id.ivClear)
        placeHolder = findViewById(R.id.ivSearchErrorPlaceholder)
        errorText = findViewById(R.id.tvSearchErrorText)
        reloadButton = findViewById(R.id.search_reload_button)
        trackListView = findViewById(R.id.track_list_view)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            // ask input edit to hide the keyboard
            inputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = clearButtonVisibility(text)
            searchText = text ?: ""
        }

        findViewById<MaterialButton>(R.id.search_reload_button).setOnClickListener {
            performSearch()
        }

        // setup track list view
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

        val searchText = inputEditText.text.toString().trim()

        if ( searchText.isEmpty() ) {
            return
        }

        itunesService.findSong(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
    }
}