package top.suhasdissa.robotcontroller.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.RobotControllerApplication
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.data.ros.Topic
import top.suhasdissa.robotcontroller.rosutil.ConnectionEvent
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeClient
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeManager

sealed class ROSConnectUiState() {
    data class Connected(
        val statusMessage: String = "Connected",
        val lastSentMessage: String = ""
    ) : ROSConnectUiState()

    data class Disconnected(
        val statusMessage: String = "Not connected",
        val lastSentMessage: String = ""
    ) : ROSConnectUiState()

    data class Loading(val statusMessage: String = "Loading...") : ROSConnectUiState()
    data class Error(val errorMessage: String) : ROSConnectUiState()
}

class ROSConnectViewModel(private val rosBridgeManager: ROSBridgeManager) : ViewModel() {

    val connectionStatus = rosBridgeManager.connectionStatus
    val connectionEvents = rosBridgeManager.connectionEvents

    private val _uiState = MutableStateFlow<ROSConnectUiState>(ROSConnectUiState.Disconnected())
    val uiState: StateFlow<ROSConnectUiState> = _uiState.asStateFlow()

    init {
        connect()
        viewModelScope.launch {
            connectionEvents.collect { event ->
                when (event) {
                    is ConnectionEvent.Connected -> {
                        _uiState.value =
                            ROSConnectUiState.Connected(statusMessage = "Connected to ROS Bridge")
                    }

                    is ConnectionEvent.Disconnected -> {
                        _uiState.value =
                            ROSConnectUiState.Disconnected(statusMessage = "Disconnected from ROS Bridge")
                    }

                    is ConnectionEvent.Error -> {
                        _uiState.value = ROSConnectUiState.Error(errorMessage = event.message)
                    }
                }
            }
        }
    }

    fun connect() {
        _uiState.value = ROSConnectUiState.Loading(statusMessage = "Connecting...")
        rosBridgeManager.connect(
            listOf(
                Topic("/robot_pose", ROSBridgeClient.MessageType.GEOMETRY_POSE2D)
            )
        )
    }

    fun publishMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is ROSConnectUiState.Connected) {
            _uiState.value = currentState.copy(lastSentMessage = message)
        }
        rosBridgeManager.publishMessage(
            Topic("/android", ROSBridgeClient.MessageType.STRING),
            Message.StringMessage(message)
        )
    }

    fun disconnect() {
        _uiState.value = ROSConnectUiState.Disconnected(statusMessage = "Disconnecting...")
        rosBridgeManager.disconnect()
    }

    fun isConnected(): Boolean {
        return rosBridgeManager.isConnected()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                ROSConnectViewModel(application.rosBridgeManager)
            }
        }
    }
}