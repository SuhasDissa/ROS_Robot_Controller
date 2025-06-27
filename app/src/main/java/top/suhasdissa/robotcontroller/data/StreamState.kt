package top.suhasdissa.robotcontroller.data

sealed class StreamState {
    data object Live : StreamState()
    data object Loading : StreamState()
    data class Error(val message: String) : StreamState()
    data object Offline : StreamState()
}