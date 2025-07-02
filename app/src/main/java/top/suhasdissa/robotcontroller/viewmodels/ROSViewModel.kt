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
import top.suhasdissa.robotcontroller.util.ConnectionEvent
import top.suhasdissa.robotcontroller.util.ROSBridgeManager
import top.suhasdissa.robotcontroller.util.ROSMessage

sealed class ROSUiState() {
    data class Connected(
        val statusMessage: String = "Connected",
        val lastSentMessage: String = ""
    ) : ROSUiState()

    data class Disconnected(
        val statusMessage: String = "Not connected",
        val lastSentMessage: String = ""
    ) : ROSUiState()

    data class Loading(val statusMessage: String = "Loading...") : ROSUiState()
    data class Error(val errorMessage: String) : ROSUiState()
}

class ROSViewModel(private val rosBridgeManager: ROSBridgeManager) : ViewModel() {

    val connectionStatus = rosBridgeManager.connectionStatus
    val receivedMessages = rosBridgeManager.receivedMessages
    val errors = rosBridgeManager.errors
    val connectionEvents = rosBridgeManager.connectionEvents

    private val _uiState = MutableStateFlow<ROSUiState>(ROSUiState.Disconnected())
    val uiState: StateFlow<ROSUiState> = _uiState.asStateFlow()

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
                            ROSUiState.Connected(statusMessage = "Connected to ROS Bridge")
                    }

                    is ConnectionEvent.Disconnected -> {
                        _uiState.value =
                            ROSUiState.Disconnected(statusMessage = "Disconnected from ROS Bridge")
                    }

                    is ConnectionEvent.Error -> {
                        _uiState.value = ROSUiState.Error(errorMessage = event.message)
                    }
                }
            }
        }
    }

    fun connectToROS() {
        _uiState.value = ROSUiState.Loading(statusMessage = "Connecting...")
        rosBridgeManager.connectToROS()
    }

    fun publishMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is ROSUiState.Connected) {
            _uiState.value = currentState.copy(lastSentMessage = message)
        }
        rosBridgeManager.publishMessage(message)
    }

    fun disconnect() {
        _uiState.value = ROSUiState.Disconnected(statusMessage = "Disconnecting...")
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
                ROSViewModel(application.rosBridgeManager)
            }
        }
    }
}