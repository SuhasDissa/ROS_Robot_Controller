package top.suhasdissa.robotcontroller.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.RobotControllerApplication
import top.suhasdissa.robotcontroller.data.AngleData
import top.suhasdissa.robotcontroller.util.RemoteController

class ControlsViewModel(private val remoteController: RemoteController) : ViewModel() {
    fun onJoystickMove(x: Float, y: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteController.publishJoystickData(x, y)
        }
    }

    fun onAngleChange(angle: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteController.publishAngles(AngleData(angle))
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                val remoteController: RemoteController = application.remoteController
                ControlsViewModel(remoteController)
            }
        }
    }
}