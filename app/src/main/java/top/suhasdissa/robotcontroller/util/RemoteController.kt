package top.suhasdissa.robotcontroller.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.data.CoordinateData

data class AngleData(val angle: Float)

data class RobotStatus(val isConnected: Boolean, val batteryLevel: Int, val currentTask: String)

interface RemoteController {
    suspend fun publishCoordinates(data: CoordinateData)
    suspend fun publishAngles(data: AngleData)
    val robotStatusFlow: SharedFlow<RobotStatus>
    val robotPosition: SharedFlow<CoordinateData?>
}

class RemoteControllerImpl : RemoteController {
    override suspend fun publishCoordinates(data: CoordinateData) = Unit
    override suspend fun publishAngles(data: AngleData) = Unit
    override val robotStatusFlow: SharedFlow<RobotStatus> = MutableSharedFlow()
    private val _robotPosition = MutableSharedFlow<CoordinateData>()
    override val robotPosition: SharedFlow<CoordinateData> = _robotPosition
    private var currentX = 0f
    private var currentY = 0f
    private var currentAngle = 0f

    private suspend fun updateRobotPosition(newX: Float, newY: Float, newAngle:Float) {
        val targetX = newX.coerceIn(0f, X_LIMIT)
        val targetY = newY.coerceIn(0f, Y_LIMIT)
        val targetAngle = newAngle.coerceIn(0f, 360f)

        val steps = 10
        val delayMillis = 50L

        val dx = (targetX - currentX) / steps
        val dy = (targetY - currentY) / steps
        val da = (targetAngle - currentAngle) / steps

        for (i in 1..steps) {
            currentX += dx
            currentY += dy
            currentAngle += da
            _robotPosition.emit(CoordinateData(currentX, currentY, currentAngle))
            delay(delayMillis)
        }
        _robotPosition.emit(CoordinateData(targetX, targetY, targetAngle))
        currentX = targetX
        currentY = targetY
        currentAngle = targetAngle
    }


    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000)
                val newX = Math.random().toFloat() * X_LIMIT
                val newY = Math.random().toFloat() * Y_LIMIT
                val newAngle = Math.random().toFloat() * 360f
                updateRobotPosition(newX, newY, newAngle)
            }
        }
    }

    companion object {
        private const val X_LIMIT = 15f
        private const val Y_LIMIT = 8f
    }
}