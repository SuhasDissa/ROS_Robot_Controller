package top.suhasdissa.robotcontroller.data.ros

enum class ROSOpType(val value: String) {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    PUBLISH("publish"),
    CALL_SERVICE("call_service"),
    SERVICE_RESPONSE("service_response")
}