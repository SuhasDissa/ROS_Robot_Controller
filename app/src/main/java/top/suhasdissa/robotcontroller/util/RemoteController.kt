package top.suhasdissa.robotcontroller.util

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.data.AngleData
import top.suhasdissa.robotcontroller.data.CoordinateData
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.data.ros.Topic
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeClient
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeManager

data class RobotStatus(val isConnected: Boolean, val batteryLevel: Int, val currentTask: String)

interface RemoteController {
    suspend fun publishCoordinates(data: CoordinateData)
    suspend fun publishAngles(data: AngleData)
    suspend fun publishJoystickData(x: Float, y: Float)
    val robotStatusFlow: SharedFlow<RobotStatus>
    val robotPosition: SharedFlow<CoordinateData?>
}

class RemoteControllerImpl(rosBridgeManager: ROSBridgeManager) : RemoteController {

    val receivedMessages = rosBridgeManager.receivedMessages
    val gson = Gson()

    init {
        rosBridgeManager.connectToROS(listOf(
            Topic("/robot_pose", ROSBridgeClient.MessageType.GEOMETRY_POSE2D)
        ))

        CoroutineScope(Dispatchers.IO).launch {
            receivedMessages.collect { message ->
                val msg = message.message.deserializeMessage<Message.Pose2DMessage>(message.message.msg,gson)

                if (msg != null) {
                    val data = CoordinateData(msg.x.toFloat(), msg.y.toFloat(), Math.toDegrees(msg.theta).toFloat())
                    publishCoordinates(data)
                }
            }
        }
    }


    override suspend fun publishCoordinates(data: CoordinateData) {
        _robotPosition.emit(data)
    }

    override suspend fun publishAngles(data: AngleData) {
        currentAngle = data.angle
        _robotPosition.emit(CoordinateData(currentX, currentY, currentAngle))
    }

    private var joystickX = 0f
    private var joystickY = 0f
    override suspend fun publishJoystickData(x: Float, y: Float) {
        joystickX = x
        joystickY = -y
//        CoroutineScope(Dispatchers.IO).launch {
//            while (isActive && (joystickX != 0f || joystickY != 0f)) {
//                updateRobotPositionWithJoystick()
//                delay(100)
//            }
//        }
    }

//    private suspend fun updateRobotPositionWithJoystick() {
//        if (joystickX != 0f || joystickY != 0f) {
//            val velocityFactor = 0.01f
//
//            currentAngle = (currentAngle + joystickX * 0.1f)
//            val angleRad = Math.toRadians(currentAngle.toDouble())
//            val deltaX = joystickY * velocityFactor * sin(angleRad).toFloat()
//            val deltaY = joystickY * velocityFactor * cos(angleRad).toFloat()
//
//
//            currentX = (currentX + deltaX).coerceIn(0f, X_LIMIT)
//            currentY = (currentY - deltaY).coerceIn(0f, Y_LIMIT)
//
//            _robotPosition.emit(CoordinateData(currentX, currentY, currentAngle))
//        }
//    }


    override val robotStatusFlow: SharedFlow<RobotStatus> = MutableSharedFlow()
    private val _robotPosition = MutableSharedFlow<CoordinateData>()
    override val robotPosition: SharedFlow<CoordinateData> = _robotPosition
    private var currentX = 5f
    private var currentY = 5f
    private var currentAngle = 0f
//
//    private suspend fun updateRobotPosition(newX: Float, newY: Float, newAngle: Float) {
//        val targetX = newX.coerceIn(0f, X_LIMIT)
//        val targetY = newY.coerceIn(0f, Y_LIMIT)
//        val targetAngle = newAngle.coerceIn(0f, 360f)
//
//        val steps = 10
//        val delayMillis = 50L
//
//        val startX = currentX
//        val startY = currentY
//        val startAngle = currentAngle
//
//        val dx = targetX - startX
//        val dy = targetY - startY
//        var angleDiff = targetAngle - startAngle
//
//
//        if (angleDiff > 180) {
//            angleDiff -= 360
//        } else if (angleDiff < -180) {
//            angleDiff += 360
//        }
//
//        for (i in 1..steps) {
//            val progress = i.toFloat() / steps
//            val easedProgress = 0.5f * (1 - cos(Math.PI * progress).toFloat())
//
//            currentX = startX + dx * easedProgress
//            currentY = startY + dy * easedProgress
//            currentAngle = (startAngle + angleDiff * easedProgress + 360) % 360
//
//            _robotPosition.emit(CoordinateData(currentX, currentY, currentAngle))
//            delay(delayMillis)
//        }
//
//        currentX = targetX
//        currentY = targetY
//        currentAngle = targetAngle
//    }

//    init {
//        CoroutineScope(Dispatchers.IO).launch {
//            _robotPosition.emit(CoordinateData(currentX, currentY, currentAngle))
//        }
//    }

    companion object {
        private const val X_LIMIT = 15f
        private const val Y_LIMIT = 8f
    }
}