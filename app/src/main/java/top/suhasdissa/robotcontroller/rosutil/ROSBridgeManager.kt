package top.suhasdissa.robotcontroller.rosutil

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.data.ros.ROSBridgeIncomingMessage
import top.suhasdissa.robotcontroller.data.ros.ROSMessage
import top.suhasdissa.robotcontroller.data.ros.Topic

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

    private val _connectionEvents = MutableStateFlow<ConnectionEvent>(ConnectionEvent.Disconnected)
    val connectionEvents: StateFlow<ConnectionEvent> = _connectionEvents

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

    fun connect(topics: List<Topic> = listOf()) {
        this.topics = topics
        rosClient.connect()
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
            for (topic in topics) {
                subscribe(topic)
            }
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
            _connectionStatus.value = false
            _connectionEvents.emit(ConnectionEvent.Error(error))
        }
    }
}