package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.utils.TimeFormatter

class PlayerViewModel(private val trackUrl: String) : ViewModel() {

    data class PlayerScreenState(
        val playerState: Int = STATE_DEFAULT,
        val progress: String = "00:00"
    )

    private val screenStateLiveData = MutableLiveData(PlayerScreenState())
    fun observeScreenState(): LiveData<PlayerScreenState> = screenStateLiveData

    private val timeFormatter: TimeFormatter = Creator.provideTimeFormatter()
    private val mediaPlayer = MediaPlayer()

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private var progressUpdateRunnable: Runnable? = null

    init {
        updateState(progress = timeFormatter.format(0L))
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        stopTimer()
    }

    fun togglePlayback() {
        when (screenStateLiveData.value?.playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        if (trackUrl.isEmpty()) {
            Log.e("PlayerActivity", "URL is null or empty")
            return
        }

        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.setOnPreparedListener {
            updateStateAsync(playerState = STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            updateStateAsync(
                playerState = STATE_PREPARED,
                progress = timeFormatter.format(0L)
            )
        }
        mediaPlayer.prepareAsync()
    }

    fun startPlayer() {
        mediaPlayer.start()
        updateState(playerState = STATE_PLAYING)
        startTimer()
    }

    fun pausePlayer() {
        stopTimer()
        mediaPlayer.pause()
        updateState(playerState = STATE_PAUSED)
    }

    private fun startTimer() {
        val runnable = createUpdateProgressTask()
        progressUpdateRunnable = runnable
        mainThreadHandler?.post(runnable)
    }

    private fun stopTimer() {
        progressUpdateRunnable?.let {
            mainThreadHandler?.removeCallbacks(it)
        }
        progressUpdateRunnable = null
    }

    private fun createUpdateProgressTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (screenStateLiveData.value?.playerState == STATE_PLAYING) {
                    val elapsedTime = mediaPlayer.currentPosition
                    updateStateAsync(progress = timeFormatter.format(elapsedTime.toLong()))
                    mainThreadHandler?.postDelayed(this, PROGRESS_UPDATE_DELAY)
                }
            }
        }
    }

    private fun updateState(
        playerState: Int? = null,
        progress: String? = null
    ) {
        val current = screenStateLiveData.value ?: PlayerScreenState()
        screenStateLiveData.value = current.copy(
            playerState = playerState ?: current.playerState,
            progress = progress ?: current.progress
        )
    }

    private fun updateStateAsync(
        playerState: Int? = null,
        progress: String? = null
    ) {
        val current = screenStateLiveData.value ?: PlayerScreenState()
        screenStateLiveData.postValue(
            current.copy(
                playerState = playerState ?: current.playerState,
                progress = progress ?: current.progress
            )
        )
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        private const val PROGRESS_UPDATE_DELAY = 50L

        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
}