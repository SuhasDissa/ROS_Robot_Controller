package top.suhasdissa.robotcontroller.viewmodels

import androidx.compose.runtime.mutableStateListOf
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
import top.suhasdissa.robotcontroller.rosutil.ConnectionEvent
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeClient
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeManager
import top.suhasdissa.robotcontroller.data.ros.ROSMessage
import top.suhasdissa.robotcontroller.data.ros.Topic

sealed class CommunicationUiState() {
    data class Connected(
        val statusMessage: String = "Connected",
        val lastSentMessage: String = ""
    ) : CommunicationUiState()

    data class Disconnected(
        val statusMessage: String = "Not connected",
        val lastSentMessage: String = ""
    ) : CommunicationUiState()

    data class Loading(val statusMessage: String = "Loading...") : CommunicationUiState()
    data class Error(val errorMessage: String) : CommunicationUiState()
}

class CommunicationTestViewModel(private val rosBridgeManager: ROSBridgeManager) : ViewModel() {

    val connectionStatus = rosBridgeManager.connectionStatus
    val receivedMessages = rosBridgeManager.receivedMessages
    val errors = rosBridgeManager.errors
    val connectionEvents = rosBridgeManager.connectionEvents

    private val _uiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Disconnected())
    val uiState: StateFlow<CommunicationUiState> = _uiState.asStateFlow()

    // Message history
    val messageHistory = mutableStateListOf<ROSMessage>()

    init {
        viewModelScope.launch {
            receivedMessages.collect { message ->
                messageHistory.add(message)
            }
        }

        viewModelScope.launch {
            connectionEvents.collect { event ->
                when (event) {
                    is ConnectionEvent.Connected -> {
                        _uiState.value =
                            CommunicationUiState.Connected(statusMessage = "Connected to ROS Bridge")
                    }

                    is ConnectionEvent.Disconnected -> {
                        _uiState.value =
                            CommunicationUiState.Disconnected(statusMessage = "Disconnected from ROS Bridge")
                    }

                    is ConnectionEvent.Error -> {
                        _uiState.value = CommunicationUiState.Error(errorMessage = event.message)
                    }
                }
            }
        }
    }

    fun connectToROS() {
        _uiState.value = CommunicationUiState.Loading(statusMessage = "Connecting...")
        rosBridgeManager.connectToROS(
            listOf(
                Topic("/rpi", ROSBridgeClient.MessageType.STRING),
                Topic("/rpi2", ROSBridgeClient.MessageType.STRING)
            )
        )
    }

    fun publishMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is CommunicationUiState.Connected) {
            _uiState.value = currentState.copy(lastSentMessage = message)
        }
        rosBridgeManager.publishMessage(
            Topic("/android", ROSBridgeClient.MessageType.STRING),
            Message.StringMessage(message)
        )
    }

    fun disconnect() {
        _uiState.value = CommunicationUiState.Disconnected(statusMessage = "Disconnecting...")
        rosBridgeManager.disconnect()
    }

    fun clearMessages() {
        messageHistory.clear()
    }

    fun isConnected(): Boolean {
        return rosBridgeManager.isConnected()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                CommunicationTestViewModel(application.rosBridgeManager)
            }
        }
    }
}