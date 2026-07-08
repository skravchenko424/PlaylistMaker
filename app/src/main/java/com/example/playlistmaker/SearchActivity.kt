package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
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
        val itunesService = retrofit.create(ITunesAPIService::class.java)

        // Search on Enter key
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = inputEditText.text.toString().trim()
                if (searchText.isNotEmpty()) {
                    performSearch(itunesService, searchText)
                } else {
                    showToast(getString(R.string.please_enter_search_term))
                }
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

    private fun performSearch(itunesService: ITunesAPIService, searchText: String) {
        // Clear previous results and show loading state
        trackList.clear()
        trackAdapter.notifyDataSetChanged()

        println("Searching for: $searchText")

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
                        showToast("No results found")
                    }
                } else {
                    // Error response
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    val errorBody = response.errorBody()?.string()
                    showToast("Error: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Network failure
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_TEXT_DEF = ""
    }
}