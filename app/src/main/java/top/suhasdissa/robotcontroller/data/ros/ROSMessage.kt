package top.suhasdissa.robotcontroller.data.ros

data class ROSMessage(
    val message: ROSBridgeIncomingMessage,
    val timestamp: Long
)