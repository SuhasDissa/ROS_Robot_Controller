package top.suhasdissa.robotcontroller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import top.suhasdissa.robotcontroller.components.DPadDirection
import top.suhasdissa.robotcontroller.ui.NavigationHost
import top.suhasdissa.robotcontroller.ui.theme.MyApplicationTheme
import top.suhasdissa.robotcontroller.viewmodels.PhysicalControllerViewModel

class MainActivity : ComponentActivity() {
    private val robotViewModel by viewModels<PhysicalControllerViewModel>(
        factoryProducer = { PhysicalControllerViewModel.Factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold { innerPadding ->
                    NavigationHost(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> robotViewModel.onDPadPressed(DPadDirection.UP)
                KeyEvent.KEYCODE_DPAD_DOWN -> robotViewModel.onDPadPressed(DPadDirection.DOWN)
                KeyEvent.KEYCODE_DPAD_LEFT -> robotViewModel.onDPadPressed(DPadDirection.LEFT)
                KeyEvent.KEYCODE_DPAD_RIGHT -> robotViewModel.onDPadPressed(DPadDirection.RIGHT)
                else -> return super.dispatchKeyEvent(event)
            }
            return true
        } else if (event.action == KeyEvent.ACTION_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> robotViewModel.onDPadReleased()
                else -> return super.dispatchKeyEvent(event)
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}