package top.suhasdissa.robotcontroller.data

sealed class LowLatencyStreamState {
    object Loading : LowLatencyStreamState()

    object Live : LowLatencyStreamState()

    object Offline : LowLatencyStreamState()

    object Connecting : LowLatencyStreamState()

    object Disconnecting : LowLatencyStreamState()

    data class Error(
        val message: String,
    ) : LowLatencyStreamState()
}
