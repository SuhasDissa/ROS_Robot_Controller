package top.suhasdissa.robotcontroller.viewmodels

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexvas.rtsp.widget.RtspStatusListener
import com.alexvas.rtsp.widget.RtspSurfaceView // Keep the import for type hinting
import top.suhasdissa.robotcontroller.data.LowLatencyStreamState
import top.suhasdissa.robotcontroller.util.Pref
import top.suhasdissa.robotcontroller.util.preferences

class LowLatencyStreamViewModel(
    application: Application,
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LowLatencyStreamViewModel"
        private const val DEBUG = true
    }

    private val _LowLatency_streamState =
        MutableLiveData<LowLatencyStreamState>(LowLatencyStreamState.Live)
    val lowLatencyStreamState: LiveData<LowLatencyStreamState> = _LowLatency_streamState

    private val streamUrl: String =
        application.applicationContext.preferences
            .getString(Pref.RTSPURLKey, Pref.DefaultRTSPURL) ?: ""

    private val rtspStatusListener =
        object : RtspStatusListener {
            override fun onRtspStatusConnecting() {
                if (DEBUG) Log.v(TAG, "onRtspStatusConnecting()")
                updateStreamState(LowLatencyStreamState.Connecting)
            }

            override fun onRtspStatusConnected() {
                if (DEBUG) Log.v(TAG, "onRtspStatusConnected()")
                updateStreamState(LowLatencyStreamState.Live)
            }

            override fun onRtspStatusDisconnecting() {
                if (DEBUG) Log.v(TAG, "onRtspStatusDisconnecting()")
                updateStreamState(LowLatencyStreamState.Disconnecting)
            }

            override fun onRtspStatusDisconnected() {
                if (DEBUG) Log.v(TAG, "onRtspStatusDisconnected()")
                updateStreamState(LowLatencyStreamState.Offline)
            }

            override fun onRtspStatusFailedUnauthorized() {
                if (DEBUG) Log.e(TAG, "onRtspStatusFailedUnauthorized()")
                updateStreamState(LowLatencyStreamState.Error("RTSP authentication failed"))
            }

            override fun onRtspStatusFailed(message: String?) {
                if (DEBUG) Log.e(TAG, "onRtspStatusFailed(message='$message')")
                updateStreamState(LowLatencyStreamState.Error(message ?: "Unknown RTSP error"))
            }

            override fun onRtspFirstFrameRendered() {
                if (DEBUG) Log.v(TAG, "onRtspFirstFrameRendered()")
                Log.i(TAG, "First frame rendered")
            }

            override fun onRtspFrameSizeChanged(
                width: Int,
                height: Int,
            ) {
                if (DEBUG) Log.v(TAG, "onRtspFrameSizeChanged(width=$width, height=$height)")
                Log.i(TAG, "Video resolution changed to ${width}x$height")
            }
        }

    init {
        if (streamUrl.isEmpty()) {
            updateStreamState(LowLatencyStreamState.Error("No RTSP URL configured"))
        }
    }

    fun getRtspStatusListener(): RtspStatusListener = rtspStatusListener

    fun updateStreamState(state: LowLatencyStreamState) {
        if (DEBUG) Log.v(TAG, "Stream state changed to: $state")
        _LowLatency_streamState.value = state
    }

    fun startStream(rtspView: RtspSurfaceView) {
        if (streamUrl.isEmpty()) return
        if (DEBUG) Log.v(TAG, "startStream()")

        try {
            if (rtspView.isStarted()) {
                Log.w(TAG, "Stream already started")
                return
            }

            updateStreamState(LowLatencyStreamState.Loading)
            val uri = streamUrl.toUri()
            rtspView.init(
                uri = uri,
                username = null,
                password = null,
                userAgent = "robot-controller-android",
            )
            rtspView.start(
                requestVideo = true,
                requestAudio = false,
                requestApplication = false,
            )
            Log.i(TAG, "Stream started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start stream", e)
            updateStreamState(LowLatencyStreamState.Error("Failed to start stream: ${e.message}"))
        }
    }

    fun stopStream(rtspView: RtspSurfaceView) {
        if (DEBUG) Log.v(TAG, "stopStream()")

        try {
            if (rtspView.isStarted()) {
                rtspView.stop()
                Log.i(TAG, "Stream stopped successfully")
            } else {
                Log.w(TAG, "Stream already stopped")
                updateStreamState(LowLatencyStreamState.Offline)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop stream", e)
            updateStreamState(LowLatencyStreamState.Error("Failed to stop stream: ${e.message}"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (DEBUG) Log.v(TAG, "ViewModel onCleared()")
    }
}
