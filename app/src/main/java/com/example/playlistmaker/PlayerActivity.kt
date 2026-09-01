package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageView
    private lateinit var progressText: TextView
    private var mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val PROGRESS_UPDATE_DELAY = 50L
    }

    private var mainThreadHandler: Handler? = null
    private var progressUpdateRunnable: Runnable? = null
    private var playerState = STATE_DEFAULT
    private var previewUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        playButton = findViewById(R.id.button_play)
        progressText = findViewById(R.id.progress)

        playButton.setOnClickListener {
            playbackControl()
        }

        val track = intent.getParcelableExtra<Track>(Track.TRACK_EXTRA_NAME)

        track?.let {
            fillTrackData(it)
        }

        preparePlayer()

        findViewById<ImageView>(R.id.back_from_player_button).setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdateTimer()
        mediaPlayer.release()
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

        previewUrl = track.previewUrl

        setVisibilityWithValue(track.collectionName, albumNameLabel, albumNameValue)
        setVisibilityWithValue(track.year, yearLabel, yearValue)
        setVisibilityWithValue(track.primaryGenreName, genreLabel, genreValue)
        setVisibilityWithValue(track.country, countryLabel, countryValue)
    }

    private fun loadAlbumCover(artworkUrl: String, imageView: ImageView) {
        // Преобразуем URL для получения изображения большего размера
        val imageUrl = artworkUrl.replace("100x100", "512x512")

        val roundingRadius = resources.getDimensionPixelSize(R.dimen.track_cover_rounding_big)
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_album_image_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
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

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        if (previewUrl.isNullOrEmpty()) {
            Log.e("PlayerActivity", "URL is null or empty")
            Toast.makeText(this, "Cannot play: No audio source", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play_button_100)
            playerState = STATE_PREPARED
            progressText.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause_button_100)
        playerState = STATE_PLAYING
        startProgressUpdateTimer()
    }

    private fun pausePlayer() {
        stopProgressUpdateTimer()
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play_button_100)
        playerState = STATE_PAUSED
    }

    private fun startProgressUpdateTimer() {
        val runnable = createUpdateProgressTask()
        progressUpdateRunnable = runnable
        mainThreadHandler?.post(runnable)
    }

    private fun stopProgressUpdateTimer() {
        progressUpdateRunnable?.let {
            mainThreadHandler?.removeCallbacks(it)
        }
        progressUpdateRunnable = null
    }

    private fun createUpdateProgressTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if(playerState == STATE_PLAYING) {
                    val elapsedTime = mediaPlayer.currentPosition
                    val seconds = elapsedTime / 1000
                    progressText.text = String.format("%d:%02d", seconds / 60, seconds % 60)

                    mainThreadHandler?.postDelayed(this, PROGRESS_UPDATE_DELAY)
                }
            }
        }
    }
}