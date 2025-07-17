package top.suhasdissa.robotcontroller.util

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.components.DPadDirection
import top.suhasdissa.robotcontroller.data.AngleData
import top.suhasdissa.robotcontroller.data.CoordinateData
import top.suhasdissa.robotcontroller.data.ros.Message
import top.suhasdissa.robotcontroller.data.ros.Topic
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeClient
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeManager

interface RemoteController {
    fun connect(topics: List<Topic>)
    fun disconnect()
    suspend fun publishCoordinates(data: CoordinateData)
    suspend fun publishAngles(data: AngleData)
    suspend fun publishJoystickData(x: Float, y: Float)
    fun publishDpad(direction: DPadDirection)

    val robotPosition: SharedFlow<CoordinateData?>
}

class RemoteControllerImpl(val rosBridgeManager: ROSBridgeManager) : RemoteController {

    val receivedMessages = rosBridgeManager.receivedMessages
    val gson = Gson()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            receivedMessages.collect { message ->
                val msg = message.message.deserializeMessage<Message.Pose2DMessage>(
                    message.message.msg,
                    gson
                )

                println(msg)

                if (msg != null) {
                    val data = CoordinateData(
                        msg.x.toFloat(),
                        msg.y.toFloat(),
                        Math.toDegrees(msg.theta).toFloat()
                    )
                    publishCoordinates(data)
                }
            }
        }
    }

    override fun connect(topics: List<Topic>) {
        rosBridgeManager.connect(topics)
    }

    override fun disconnect() {
        rosBridgeManager.disconnect()
    }

    override suspend fun publishCoordinates(data: CoordinateData) {
        _robotPosition.emit(data)
    }

    override suspend fun publishAngles(data: AngleData) {

    }

    private var joystickX = 0f
    private var joystickY = 0f
    override suspend fun publishJoystickData(x: Float, y: Float) {
        joystickX = x
        joystickY = -y
    }

    override fun publishDpad(direction: DPadDirection) {
        val topic = Topic("/remote_keys", ROSBridgeClient.MessageType.STRING)
        rosBridgeManager.publishMessage(topic, Message.StringMessage(direction.key))
    }

    private val _robotPosition = MutableSharedFlow<CoordinateData>()
    override val robotPosition: SharedFlow<CoordinateData> = _robotPosition
}