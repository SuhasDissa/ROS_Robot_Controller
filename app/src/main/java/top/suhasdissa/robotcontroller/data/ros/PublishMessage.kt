package top.suhasdissa.robotcontroller.data.ros

data class PublishMessage(
    val op: String = ROSOpType.PUBLISH.value,
    val topic: String,
    val type: String,
    val msg: Message
)