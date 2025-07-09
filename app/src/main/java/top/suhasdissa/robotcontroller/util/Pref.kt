package top.suhasdissa.robotcontroller.util

object Pref {
    const val RTSPURLKey = "rtsp_url"
    const val WebsocketURLKey = "websocket_url"
    const val useLowLatencyStream = "low_latency_stream"

    const val DefaultWebsocketURL = "ws://192.168.8.161:9090"
    const val DefaultRTSPURL = "rtsp://192.168.8.184:5555"
    const val SHARED_PREFS_NAME = "app_prefs"
}
