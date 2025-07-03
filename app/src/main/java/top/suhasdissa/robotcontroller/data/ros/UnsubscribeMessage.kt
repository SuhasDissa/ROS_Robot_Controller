package top.suhasdissa.robotcontroller.data.ros

data class UnsubscribeMessage(
    val op: String = ROSOpType.UNSUBSCRIBE.value,
    val topic: String
)