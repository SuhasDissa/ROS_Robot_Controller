package top.suhasdissa.robotcontroller

import android.app.Application
import top.suhasdissa.robotcontroller.util.RemoteController
import top.suhasdissa.robotcontroller.util.RemoteControllerImpl

class RobotControllerApplication : Application() {
    val remoteController: RemoteController by lazy { RemoteControllerImpl() }
}