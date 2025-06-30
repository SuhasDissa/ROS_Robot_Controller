package top.suhasdissa.robotcontroller.util

import com.google.gson.JsonObject

class ROSBridgeManager : ROSBridgeClient.ROSBridgeListener {
    private lateinit var rosClient: ROSBridgeClient

    fun initialize() {
        rosClient = ROSBridgeClient("ws://YOUR_ROS_SERVER_IP:9090")
        rosClient.setListener(this)
    }

    private fun connectToROS() {
        rosClient.connect()
    }

    private fun subscribeToTopic() {
        rosClient.subscribe("/chatter", ROSBridgeClient.MessageType.STRING)
    }

    private fun publishMessage() {
        val message = JsonObject().apply {
            addProperty("data", "Hello from Android!")
        }
        rosClient.publish("/chatter", ROSBridgeClient.MessageType.STRING, message)
    }

    override fun onConnected() {
        subscribeToTopic()
    }

    override fun onDisconnected() {

    }

    override fun onMessageReceived(topic: String, message: JsonObject) {
        val msg = message.getAsJsonObject("msg")
        msg?.get("data")?.asString
    }

    override fun onError(error: String) {

    }
}