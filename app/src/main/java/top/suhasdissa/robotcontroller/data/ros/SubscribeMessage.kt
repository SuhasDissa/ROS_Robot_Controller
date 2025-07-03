package top.suhasdissa.robotcontroller.data.ros

data class SubscribeMessage(
    val op: String = ROSOpType.SUBSCRIBE.value,
    val topic: String,
    val type: String
)

