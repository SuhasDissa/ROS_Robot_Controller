package top.suhasdissa.robotcontroller.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
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
import top.suhasdissa.robotcontroller.data.Doughnut
import top.suhasdissa.robotcontroller.util.RemoteController

class ArenaMapViewModel(private val remoteController: RemoteController) : ViewModel() {
    val selectedPoint: MutableLiveData<CoordinateData?> = MutableLiveData(null)
    val robotPosition = remoteController.robotPosition
        .asLiveData(viewModelScope.coroutineContext)

    val circleData: LiveData<Doughnut?> = remoteController.circleData.asLiveData(viewModelScope.coroutineContext)

    private val _circleData2 = MutableLiveData<Doughnut?>()
    val circleData2: LiveData<Doughnut?> = _circleData2

    init {
        //_circleData.value = Doughnut(5f to 2f, 2f, 3f, Color.Green)
        _circleData2.value = Doughnut(10f to 5f, 3f, 4f, Color.Yellow)
    }

    fun onCoordinateSelected(x: Float, y: Float) {
        selectedPoint.value = CoordinateData(x, y)
       // _circleData.value = Doughnut(x to y, 2f, 3f, Color.Green)
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