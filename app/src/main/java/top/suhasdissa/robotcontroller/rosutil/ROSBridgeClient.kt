package top.suhasdissa.robotcontroller.rosutil

import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import top.suhasdissa.robotcontroller.data.ros.CallServiceMessage
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.data.ros.PublishMessage
import top.suhasdissa.robotcontroller.data.ros.ROSBridgeIncomingMessage
import top.suhasdissa.robotcontroller.data.ros.ROSOpType
import top.suhasdissa.robotcontroller.data.ros.SubscribeMessage
import top.suhasdissa.robotcontroller.data.ros.Topic
import top.suhasdissa.robotcontroller.data.ros.UnsubscribeMessage
import java.net.URI

class ROSBridgeClient(private var serverUri: String) {
    private var webSocketClient: WebSocketClient? = null
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
            webSocketClient?.closeBlocking()
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
                ROSOpType.PUBLISH.value -> {
                    listener?.onMessageReceived(incomingMessage)
                }

                ROSOpType.SERVICE_RESPONSE.value -> {
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