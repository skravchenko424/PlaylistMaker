package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra<Track>("track")

        track?.let {
            fillTrackData(it)
        }

        findViewById<ImageView>(R.id.back_from_player_button).setOnClickListener {
            finish()
        }
    }

    private fun fillTrackData(track: Track) {
        val albumCover = findViewById<ImageView>(R.id.album_cover)
        val songName = findViewById<TextView>(R.id.song_name)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val time = findViewById<TextView>(R.id.progress)
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
        durationValue.text = formatTrackTime(track.trackTimeMillis)

        loadAlbumCover(track.artworkUrl100, albumCover)

        setVisibilityWithValue(track.collectionName, albumNameLabel, albumNameValue)
        setVisibilityWithValue(track.year, yearLabel, yearValue)
        setVisibilityWithValue(track.primaryGenreName, genreLabel, genreValue)
        setVisibilityWithValue(track.country, countryLabel, countryValue)
    }

    private fun loadAlbumCover(artworkUrl: String, imageView: ImageView) {
        // Преобразуем URL для получения изображения большего размера
        val imageUrl = artworkUrl.replace("100x100", "512x512")

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_album_image_placeholder_312)
            .centerCrop()
            .into(imageView)
    }

    private fun formatTrackTime(timeMillis: Long): String {
        return if (timeMillis > 0) {
            val minutes = (timeMillis / 1000) / 60
            val seconds = (timeMillis / 1000) % 60
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        } else {
            "0:00"
        }
    }

    private fun <T> setVisibilityWithValue(
        value: T?,
        labelView: android.view.View,
        valueView: android.widget.TextView,
        formatter: ((T) -> String)? = null
    ) {
        if (value != null) {
            labelView.visibility = android.view.View.VISIBLE
            valueView.text = if (formatter != null) formatter(value) else value.toString()
            valueView.visibility = android.view.View.VISIBLE
        } else {
            labelView.visibility = android.view.View.GONE
            valueView.visibility = android.view.View.GONE
        }
    }
}