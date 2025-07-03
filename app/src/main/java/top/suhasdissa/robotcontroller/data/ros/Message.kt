package top.suhasdissa.robotcontroller.data.ros

sealed class Message {
    data class StringMessage(val data: String) : Message()
    data class Int32Message(val data: Int) : Message()
    data class BoolMessage(val data: Boolean) : Message()
    data class Float32Message(val data: Float) : Message()
    data class Float64Message(val data: Double) : Message()
    data class Pose2DMessage(
        val x: Double = 0.0,
        val y: Double = 0.0,
        val theta: Double = 0.0
    ) : Message()
}