package top.suhasdissa.robotcontroller.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class ROSBridgeClient(private val serverUri: String) {
    private var webSocketClient: WebSocketClient? = null
    private val gson = Gson()

    interface ROSBridgeListener {
        fun onConnected()
        fun onDisconnected()
        fun onMessageReceived(topic: String, message: JsonObject)
        fun onError(error: String)
    }

    enum class MessageType(val value: String) {
        STRING("std_msgs/String"),
        INT32("std_msgs/Int32"),
        BOOL("std_msgs/Bool")
    }

    private var listener: ROSBridgeListener? = null

    fun setListener(listener: ROSBridgeListener) {
        this.listener = listener
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
            val jsonObject = gson.fromJson(message, JsonObject::class.java)
            val topic = jsonObject.get("topic")?.asString
            if (topic != null) {
                listener?.onMessageReceived(topic, jsonObject)
            }
        } catch (e: Exception) {
            listener?.onError("Message parsing error: ${e.message}")
        }
    }

    fun subscribe(topic: String, messageType: MessageType) {
        val subscribeMsg = JsonObject().apply {
            addProperty("op", "subscribe")
            addProperty("topic", topic)
            addProperty("type", messageType.value)
        }
        sendMessage(subscribeMsg.toString())
    }

    fun unsubscribe(topic: String) {
        val unsubscribeMsg = JsonObject().apply {
            addProperty("op", "unsubscribe")
            addProperty("topic", topic)
        }
        sendMessage(unsubscribeMsg.toString())
    }

    fun publish(topic: String, messageType: MessageType, message: JsonObject) {
        val publishMsg = JsonObject().apply {
            addProperty("op", "publish")
            addProperty("topic", topic)
            addProperty("type", messageType.value)
            add("msg", message)
        }
        sendMessage(publishMsg.toString())
    }

    fun callService(service: String, serviceType: String, args: JsonObject? = null) {
        val serviceMsg = JsonObject().apply {
            addProperty("op", "call_service")
            addProperty("service", service)
            addProperty("type", serviceType)
            args?.let { add("args", it) }
        }
        sendMessage(serviceMsg.toString())
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