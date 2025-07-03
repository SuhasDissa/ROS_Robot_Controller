package top.suhasdissa.robotcontroller.data.ros

data class CallServiceMessage(
    val op: String = ROSOpType.CALL_SERVICE.value,
    val service: String,
    val type: String,
    val args: Any? = null
)