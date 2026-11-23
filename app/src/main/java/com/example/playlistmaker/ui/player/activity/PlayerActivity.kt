package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel.Companion.STATE_PAUSED
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel.Companion.STATE_PLAYING
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel.Companion.STATE_PREPARED
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private var track: Track? = null

    private lateinit var playButton: ImageView
    private lateinit var progressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        track = intent.getParcelableExtra(Track.TRACK_EXTRA_NAME)
        viewModel = getViewModel {
            parametersOf(track?.previewUrl ?: "")
        }

        progressText = findViewById(R.id.progress)

        playButton = findViewById(R.id.button_play)
        playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        track?.let {
            fillTrackData(it)
        }

        viewModel.observeScreenState().observe(this) {
            progressText.text = it.progress
            when(it.playerState) {
                STATE_PLAYING -> playButton.setImageResource(R.drawable.ic_pause_button_100)
                STATE_PREPARED, STATE_PAUSED -> playButton.setImageResource(R.drawable.ic_play_button_100)
            }
        }

        findViewById<ImageView>(R.id.back_from_player_button).setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun fillTrackData(track: Track) {
        val albumCover = findViewById<ImageView>(R.id.album_cover)
        val songName = findViewById<TextView>(R.id.song_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val durationValue = findViewById<TextView>(R.id.duration_value)

        val albumNameLabel = findViewById<TextView>(R.id.album_name)
        val albumNameValue = findViewById<TextView>(R.id.album_name_value)
        val yearLabel = findViewById<TextView>(R.id.year)
        val yearValue = findViewById<TextView>(R.id.year_value)
        val genreLabel = findViewById<TextView>(R.id.genre)
        val genreValue = findViewById<TextView>(R.id.genre_value)
        val countryLabel = findViewById<TextView>(R.id.country)
        val countryValue = findViewById<TextView>(R.id.country_value)

        songName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.trackTime

        loadAlbumCover(track.artworkUrl512, albumCover)

        setVisibilityWithValue(track.collectionName, albumNameLabel, albumNameValue)
        setVisibilityWithValue(track.year, yearLabel, yearValue)
        setVisibilityWithValue(track.primaryGenreName, genreLabel, genreValue)
        setVisibilityWithValue(track.country, countryLabel, countryValue)
    }

    private fun loadAlbumCover(artworkUrl: String, imageView: ImageView) {
        val roundingRadius = resources.getDimensionPixelSize(R.dimen.track_cover_rounding_big)
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.ic_album_image_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(imageView)
    }

    private fun <T> setVisibilityWithValue(
        value: T?,
        labelView: View,
        valueView: TextView,
        formatter: ((T) -> String)? = null
    ) {
        if (value != null) {
            labelView.visibility = View.VISIBLE
            valueView.text = if (formatter != null) formatter(value) else value.toString()
            valueView.visibility = View.VISIBLE
        } else {
            labelView.visibility = View.GONE
            valueView.visibility = View.GONE
        }
    }
}