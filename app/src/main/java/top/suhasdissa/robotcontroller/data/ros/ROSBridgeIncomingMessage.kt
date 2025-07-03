package top.suhasdissa.robotcontroller.data.ros

import com.google.gson.Gson
import com.google.gson.JsonObject

data class ROSBridgeIncomingMessage(
    val op: String,
    val topic: String,
    val msg: JsonObject,
    val type: String,
    val service: String,
    val values: JsonObject,
    val result: Boolean
) {
    inline fun <reified T : Message> deserializeMessage(jsonMsg: JsonObject?, gson: Gson): T? {
        if (jsonMsg == null) return null
        return when (T::class) {
            Message.StringMessage::class -> gson.fromJson(
                jsonMsg,
                Message.StringMessage::class.java
            ) as? T

            Message.Int32Message::class -> gson.fromJson(
                jsonMsg,
                Message.Int32Message::class.java
            ) as? T

            Message.BoolMessage::class -> gson.fromJson(
                jsonMsg,
                Message.BoolMessage::class.java
            ) as? T

            Message.Float32Message::class -> gson.fromJson(
                jsonMsg,
                Message.Float32Message::class.java
            ) as? T

            Message.Float64Message::class -> gson.fromJson(
                jsonMsg,
                Message.Float64Message::class.java
            ) as? T

            Message.Pose2DMessage::class -> gson.fromJson(
                jsonMsg,
                Message.Pose2DMessage::class.java
            ) as? T

            else -> null
        }
    }
}