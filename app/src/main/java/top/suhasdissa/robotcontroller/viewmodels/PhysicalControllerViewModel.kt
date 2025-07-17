package top.suhasdissa.robotcontroller.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.RobotControllerApplication
import top.suhasdissa.robotcontroller.components.DPadDirection
import top.suhasdissa.robotcontroller.util.RemoteController

class PhysicalControllerViewModel(private val remoteController: RemoteController) : ViewModel() {
    fun onDPadReleased() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteController.publishDpad(DPadDirection.CENTER)
            Log.d("Phys", "onDPadRelease")
        }
    }

    fun onDPadPressed(direction: DPadDirection) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteController.publishDpad(direction)
            Log.d("Phys", "onDPadPressed: $direction")
        }

    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                val remoteController: RemoteController = application.remoteController
                PhysicalControllerViewModel(remoteController)
            }
        }
    }
}