package top.suhasdissa.robotcontroller.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.suhasdissa.robotcontroller.RobotControllerApplication
import top.suhasdissa.robotcontroller.data.CoordinateData
import top.suhasdissa.robotcontroller.util.RemoteController

class ArenaMapViewModel(private val remoteController: RemoteController) : ViewModel() {
    val selectedPoint: MutableLiveData<CoordinateData?> = MutableLiveData(null)
    val robotPosition = remoteController.robotPosition
        .asLiveData(viewModelScope.coroutineContext)

    fun onCoordinateSelected(x: Float, y: Float) {
        selectedPoint.value = CoordinateData(x, y)
        viewModelScope.launch(Dispatchers.IO) {
            remoteController.publishCoordinates(CoordinateData(x, y))
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as RobotControllerApplication)
                val remoteController: RemoteController = application.remoteController
                ArenaMapViewModel(remoteController)
            }
        }
    }
}