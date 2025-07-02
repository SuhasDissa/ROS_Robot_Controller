package top.suhasdissa.robotcontroller.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.RobotControllerApplication

class SettingsViewModel(context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            RobotControllerApplication.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val _rtspUrl = MutableStateFlow(getRtspUrl())
    val rtspUrl: StateFlow<String> = _rtspUrl.asStateFlow()

    private val _websocketUrl = MutableStateFlow(getWebsocketUrl())
    val websocketUrl: StateFlow<String> = _websocketUrl.asStateFlow()

    private val _showRtspDialog = MutableStateFlow(false)
    val showRtspDialog: StateFlow<Boolean> = _showRtspDialog.asStateFlow()

    private val _showWebsocketDialog = MutableStateFlow(false)
    val showWebsocketDialog: StateFlow<Boolean> = _showWebsocketDialog.asStateFlow()

    private fun getRtspUrl(): String {
        return sharedPreferences.getString(
            "rtsp_url",
            RobotControllerApplication.DEFAULT_RTSP_URL
        )!!
    }

    private fun getWebsocketUrl(): String {
        return sharedPreferences.getString(
            "websocket_url",
            RobotControllerApplication.DEFAULT_WEBSOCKET_URL
        )!!
    }

    fun saveRtspUrl(url: String) {
        viewModelScope.launch {
            sharedPreferences.edit { putString("rtsp_url", url) }
            _rtspUrl.value = url
        }
    }

    fun saveWebsocketUrl(url: String) {
        viewModelScope.launch {
            sharedPreferences.edit { putString("websocket_url", url) }
            _websocketUrl.value = url
        }
    }

    fun showRtspDialog() {
        _showRtspDialog.value = true
    }

    fun hideRtspDialog() {
        _showRtspDialog.value = false
    }

    fun showWebsocketDialog() {
        _showWebsocketDialog.value = true
    }

    fun hideWebsocketDialog() {
        _showWebsocketDialog.value = false
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                SettingsViewModel(application.applicationContext)
            }
        }
    }
}