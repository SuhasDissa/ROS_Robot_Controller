package top.suhasdissa.robotcontroller.data.ros

import top.suhasdissa.robotcontroller.rosutil.ROSBridgeClient

data class Topic(
    val topic: String,
    val messageType: ROSBridgeClient.MessageType
)