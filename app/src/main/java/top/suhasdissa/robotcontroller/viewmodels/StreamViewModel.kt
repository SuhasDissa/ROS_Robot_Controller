package top.suhasdissa.robotcontroller.viewmodels

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import top.suhasdissa.robotcontroller.data.StreamState
import top.suhasdissa.robotcontroller.util.Pref
import top.suhasdissa.robotcontroller.util.preferences

class StreamViewModel(application: Application) : AndroidViewModel(application) {
    private val _streamState = MutableLiveData<StreamState>(StreamState.Loading)
    val streamState: LiveData<StreamState> = _streamState

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer? get() = _exoPlayer

    init {
        initializePlayer(application.applicationContext)
        val rtspUrl =
            application.applicationContext.preferences.getString(
                Pref.RTSPURLKey,
                Pref.DefaultRTSPURL
            )
        startStream(rtspUrl!!)
    }

    private fun initializePlayer(context: Context) {
        _exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            updateStreamState(StreamState.Live)
                        }

                        Player.STATE_READY -> {
                            updateStreamState(StreamState.Live)
                        }

                        Player.STATE_ENDED -> {
                            updateStreamState(StreamState.Offline)
                        }

                        Player.STATE_IDLE -> {
                            updateStreamState(StreamState.Loading)
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    val errorMessage = when (error.errorCode) {
                        PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> "Stream is too far behind live window"

                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network connection failed"

                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Connection timeout"

                        PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "Invalid stream format"

                        else -> error.message ?: "Unknown playback error"
                    }
                    updateStreamState(StreamState.Error(errorMessage))
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    if (isLoading && _streamState.value !is StreamState.Loading) {
                        updateStreamState(StreamState.Loading)
                    }
                }
            })
        }
    }

    fun updateStreamState(state: StreamState) {
        _streamState.value = state
    }

    @OptIn(UnstableApi::class)
    fun startStream(url: String) {
        val mediaSource: MediaSource =
            RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(url))

        _exoPlayer?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }

        _streamState.value = StreamState.Live
    }

    fun stopStream() {
        _exoPlayer?.stop()
        _streamState.value = StreamState.Offline
    }

    override fun onCleared() {
        super.onCleared()
        _exoPlayer?.release()
    }
}