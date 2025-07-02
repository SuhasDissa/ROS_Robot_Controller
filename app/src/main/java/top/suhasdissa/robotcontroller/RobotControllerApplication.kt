package top.suhasdissa.robotcontroller

import android.app.Application
import top.suhasdissa.robotcontroller.util.ROSBridgeManager
import top.suhasdissa.robotcontroller.util.RemoteController
import top.suhasdissa.robotcontroller.util.RemoteControllerImpl

class RobotControllerApplication : Application() {
    val remoteController: RemoteController by lazy { RemoteControllerImpl() }

    val rosBridgeManager: ROSBridgeManager by lazy {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val webSocketUrl = sharedPreferences.getString("websocket_url", DEFAULT_WEBSOCKET_URL)!!
        ROSBridgeManager.getInstance(webSocketUrl)
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize ROS connection on app start
        rosBridgeManager.connectToROS()
    }

    override fun onTerminate() {
        super.onTerminate()
        rosBridgeManager.disconnect()
    }

    companion object {
        const val DEFAULT_WEBSOCKET_URL = "ws://192.168.8.161:9090"
        const val DEFAULT_RTSP_URL = "rtsp://192.168.8.184:5555"
        const val SHARED_PREFS_NAME = "app_prefs"
    }
}