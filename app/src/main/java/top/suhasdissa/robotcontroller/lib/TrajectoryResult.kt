package top.suhasdissa.robotcontroller.lib

data class TrajectoryResult(
    val angle: Double,
    val velocity: Double,
    val success: Boolean
)