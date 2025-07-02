package top.suhasdissa.robotcontroller.util

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ROSMessage(
    val topic: String,
    val data: String,
    val timestamp: Long
)

sealed class ConnectionEvent {
    object Connected : ConnectionEvent()
    object Disconnected : ConnectionEvent()
    data class Error(val message: String) : ConnectionEvent()
}

class ROSBridgeManager private constructor(serverUri: String) : ROSBridgeClient.ROSBridgeListener {
    private var rosClient: ROSBridgeClient = ROSBridgeClient(serverUri)

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _receivedMessages = MutableSharedFlow<ROSMessage>()
    val receivedMessages: SharedFlow<ROSMessage> = _receivedMessages

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors

    private val _connectionEvents = MutableStateFlow<ConnectionEvent>(ConnectionEvent.Disconnected)
    val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents

    companion object {
        @Volatile
        private var INSTANCE: ROSBridgeManager? = null

        fun getInstance(serverUri: String = "ws://192.168.8.161:9090"): ROSBridgeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ROSBridgeManager(serverUri).also { INSTANCE = it }
            }
        }
    }

    init {
        rosClient.setListener(this)
    }

    fun connectToROS() {
        rosClient.connect()
    }

    private fun subscribeToTopic() {
        rosClient.subscribe("/rpi", ROSBridgeClient.MessageType.STRING)
    }

    fun publishMessage(data: String = "Hello from Android!") {
        val message = JsonObject().apply {
            addProperty("data", data)
        }
        rosClient.publish("/android", ROSBridgeClient.MessageType.STRING, message)
    }

    fun subscribe(topic: String, messageType: ROSBridgeClient.MessageType) {
        rosClient.subscribe(topic, messageType)
    }

    fun unsubscribe(topic: String) {
        rosClient.unsubscribe(topic)
    }

    fun callService(service: String, serviceType: String, args: JsonObject? = null) {
        rosClient.callService(service, serviceType, args)
    }

    fun disconnect() {
        rosClient.disconnect()
        _connectionStatus.value = false
    }

    fun isConnected(): Boolean {
        return rosClient.isConnected()
    }

    override fun onConnected() {
        CoroutineScope(Dispatchers.IO).launch {
            _connectionStatus.value = true
            _connectionEvents.emit(ConnectionEvent.Connected)
        }
        subscribeToTopic()
    }

    override fun onDisconnected() {
        CoroutineScope(Dispatchers.IO).launch {
            _connectionStatus.value = false
            _connectionEvents.emit(ConnectionEvent.Disconnected)
        }
    }

    override fun onMessageReceived(topic: String, message: JsonObject) {
        CoroutineScope(Dispatchers.IO).launch {
            val msg = message.getAsJsonObject("msg")
            val data = msg?.get("data")?.asString ?: ""
            val timestamp = System.currentTimeMillis()
            _receivedMessages.emit(ROSMessage(topic, data, timestamp))
        }
    }

    override fun onError(error: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _errors.emit(error)
            _connectionEvents.emit(ConnectionEvent.Error(error))
        }
    }
}