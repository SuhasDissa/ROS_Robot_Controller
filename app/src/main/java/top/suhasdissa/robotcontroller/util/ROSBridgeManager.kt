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
    val message: ROSBridgeIncomingMessage,
    val timestamp: Long
)

data class Topic(
    val topic: String,
    val messageType: ROSBridgeClient.MessageType
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
    val receivedMessages: MutableSharedFlow<ROSMessage> = _receivedMessages

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors

    private val _connectionEvents = MutableStateFlow<ConnectionEvent>(ConnectionEvent.Disconnected)
    val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents

    var topics = listOf<Topic>()

    companion object {
        @Volatile
        private var INSTANCE: ROSBridgeManager? = null

        fun getInstance(serverUri: String): ROSBridgeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ROSBridgeManager(serverUri).also { INSTANCE = it }
            }
        }
    }

    init {
        rosClient.setListener(this)
    }

    fun connectToROS(topics: List<Topic> = listOf()) {
        this.topics = topics
        rosClient.connect()
    }

    private fun subscribeToTopic(topic: Topic) {
        rosClient.subscribe(topic)
    }

    fun publishMessage(topic: Topic, message: Message) {
        rosClient.publish(topic, message)
    }

    fun subscribe(topic: Topic) {
        rosClient.subscribe(topic)
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
        for (topic in topics) {
            subscribeToTopic(topic)
        }
    }

    override fun onDisconnected() {
        CoroutineScope(Dispatchers.IO).launch {
            _connectionStatus.value = false
            _connectionEvents.emit(ConnectionEvent.Disconnected)
        }
    }

    override fun onMessageReceived(message: ROSBridgeIncomingMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            val timestamp = System.currentTimeMillis()
            _receivedMessages.emit(ROSMessage(message, timestamp))
        }
    }

    override fun onServiceResponse(
        service: String,
        result: Boolean,
        values: Any?
    ) {

    }

    override fun onError(error: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _errors.emit(error)
            _connectionEvents.emit(ConnectionEvent.Error(error))
        }
    }
}