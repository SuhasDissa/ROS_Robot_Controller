package top.suhasdissa.robotcontroller

import android.app.Application
import top.suhasdissa.robotcontroller.rosutil.ROSBridgeManager
import top.suhasdissa.robotcontroller.util.Pref
import top.suhasdissa.robotcontroller.util.RemoteController
import top.suhasdissa.robotcontroller.util.RemoteControllerImpl

class RobotControllerApplication : Application() {
    val remoteController: RemoteController by lazy { RemoteControllerImpl(rosBridgeManager) }

    val rosBridgeManager: ROSBridgeManager by lazy {
        val sharedPreferences = getSharedPreferences(Pref.SHARED_PREFS_NAME, MODE_PRIVATE)
        val webSocketUrl =
            sharedPreferences.getString(Pref.WebsocketURLKey, Pref.DefaultWebsocketURL)!!
        ROSBridgeManager.getInstance(webSocketUrl)
    }

    override fun onCreate() {
        super.onCreate()
        // rosBridgeManager.connect()
    }

    override fun onTerminate() {
        super.onTerminate()
        rosBridgeManager.disconnect()
    }
}