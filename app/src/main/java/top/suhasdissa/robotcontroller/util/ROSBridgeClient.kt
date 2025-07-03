package top.suhasdissa.robotcontroller.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

data class SubscribeMessage(
    val op: String = "subscribe",
    val topic: String,
    val type: String
)

data class UnsubscribeMessage(
    val op: String = "unsubscribe",
    val topic: String
)

data class PublishMessage(
    val op: String = "publish",
    val topic: String,
    val type: String,
    val msg: Message
)

data class CallServiceMessage(
    val op: String = "call_service",
    val service: String,
    val type: String,
    val args: Any? = null
)

data class ROSBridgeIncomingMessage(
    val op: String,
    val topic: String,
    val msg: JsonObject,
    val type: String,
    val service: String,
    val values: JsonObject,
    val result: Boolean
) {
    inline fun <reified T : Message> deserializeMessage(jsonMsg: JsonObject?): T? {
        if (jsonMsg == null) return null
        return when (T::class) {
            Message.StringMessage::class -> Gson().fromJson(
                jsonMsg,
                Message.StringMessage::class.java
            ) as? T

            Message.Int32Message::class -> Gson().fromJson(
                jsonMsg,
                Message.Int32Message::class.java
            ) as? T

            Message.BoolMessage::class -> Gson().fromJson(
                jsonMsg,
                Message.BoolMessage::class.java
            ) as? T

            Message.Float32Message::class -> Gson().fromJson(
                jsonMsg,
                Message.Float32Message::class.java
            ) as? T

            Message.Float64Message::class -> Gson().fromJson(
                jsonMsg,
                Message.Float64Message::class.java
            ) as? T

            Message.Pose2DMessage::class -> Gson().fromJson(
                jsonMsg,
                Message.Pose2DMessage::class.java
            ) as? T

            else -> null
        }
    }
}

sealed class Message {
    data class StringMessage(val data: String) : Message()
    data class Int32Message(val data: Int) : Message()
    data class BoolMessage(val data: Boolean) : Message()
    data class Float32Message(val data: Float) : Message()
    data class Float64Message(val data: Double) : Message()
    data class Pose2DMessage(
        val x: Double = 0.0,
        val y: Double = 0.0,
        val theta: Double = 0.0
    ) : Message()
}


class ROSBridgeClient(private var serverUri: String) {
    private var webSocketClient: WebSocketClient? = null // NOSONAR
    private val gson = Gson()

    interface ROSBridgeListener {
        fun onConnected()
        fun onDisconnected()
        fun onMessageReceived(message: ROSBridgeIncomingMessage)
        fun onServiceResponse(service: String, result: Boolean, values: Any?)
        fun onError(error: String)
    }

    enum class MessageType(val value: String) {
        STRING("std_msgs/String"),
        INT32("std_msgs/Int32"),
        BOOL("std_msgs/Bool"),
        FLOAT32("std_msgs/Float32"),
        FLOAT64("std_msgs/Float64"),
        GEOMETRY_TWIST("geometry_msgs/Twist"),
        SENSOR_IMAGE("sensor_msgs/Image"),
        GEOMETRY_POSE2D("geometry_msgs/Pose2D"),
        CUSTOM("custom")
    }

    private var listener: ROSBridgeListener? = null

    fun setListener(listener: ROSBridgeListener) {
        this.listener = listener
    }

    fun setServerUri(uri: String) {
        this.serverUri = uri
    }

    fun connect() {
        try {
            val uri = URI(serverUri)
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshake: ServerHandshake?) {
                    listener?.onConnected()
                }

                override fun onMessage(message: String?) {
                    message?.let { handleMessage(it) }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    listener?.onDisconnected()
                }

                override fun onError(ex: Exception?) {
                    listener?.onError(ex?.message ?: "Unknown error")
                }
            }
            webSocketClient?.connect()
        } catch (e: Exception) {
            listener?.onError("Connection failed: ${e.message}")
        }
    }

    private fun handleMessage(message: String) {
        try {
            val incomingMessage = gson.fromJson(message, ROSBridgeIncomingMessage::class.java)

            when (incomingMessage.op) {
                "publish" -> {
                    listener?.onMessageReceived(incomingMessage)
                }

                "service_response" -> {
                    incomingMessage.service.let { service ->
                        listener?.onServiceResponse(
                            service,
                            incomingMessage.result,
                            incomingMessage.values
                        )
                    }
                }

                else -> {
                    listener?.onError("Unknown message type: ${incomingMessage.op}")
                }
            }
        } catch (e: Exception) {
            listener?.onError("Message parsing error: ${e.message}")
        }
    }

    fun subscribe(topic: Topic) {
        val subscribeMsg = SubscribeMessage(
            topic = topic.topic,
            type = topic.messageType.value
        )
        sendMessage(gson.toJson(subscribeMsg))
    }

    fun unsubscribe(topic: String) {
        val unsubscribeMsg = UnsubscribeMessage(topic = topic)
        sendMessage(gson.toJson(unsubscribeMsg))
    }

    // Type-safe publish methods for standard message types
    fun publishString(topic: Topic, data: String) {
        val message = Message.StringMessage(data)
        val publishMsg = PublishMessage(
            topic = topic.topic,
            type = MessageType.STRING.value,
            msg = message
        )
        sendMessage(gson.toJson(publishMsg))
    }

    fun publishInt32(topic: String, data: Int) {
        val message = Message.Int32Message(data)
        val publishMsg = PublishMessage(
            topic = topic,
            type = MessageType.INT32.value,
            msg = message
        )
        sendMessage(gson.toJson(publishMsg))
    }

    fun publishBool(topic: String, data: Boolean) {
        val message = Message.BoolMessage(data)
        val publishMsg = PublishMessage(
            topic = topic,
            type = MessageType.BOOL.value,
            msg = message
        )
        sendMessage(gson.toJson(publishMsg))
    }

    fun publish(topic: Topic, message: Message) {
        val publishMsg = PublishMessage(
            topic = topic.topic,
            type = topic.messageType.value,
            msg = message
        )
        sendMessage(gson.toJson(publishMsg))
    }

    fun callService(service: String, serviceType: String, args: Any? = null) {
        val serviceMsg = CallServiceMessage(
            service = service,
            type = serviceType,
            args = args
        )
        sendMessage(gson.toJson(serviceMsg))
    }

    private fun sendMessage(message: String) {
        webSocketClient?.send(message)
    }

    fun disconnect() {
        webSocketClient?.close()
    }

    fun isConnected(): Boolean {
        return webSocketClient?.isOpen == true
    }
}

