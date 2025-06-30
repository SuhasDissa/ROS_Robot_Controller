package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suhasdissa.robotcontroller.viewmodels.ControlsViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun JoystickControl(
    darkGrey: Color, lightGrey: Color
) {
    val controlsViewModel: ControlsViewModel = viewModel(factory = ControlsViewModel.Factory)
    var joystickPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val joystickRadius = with(density) { 60.dp.toPx() }
    val stickRadius = with(density) { 25.dp.toPx() }
    val maxDistance = joystickRadius - stickRadius

    Box(
        modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            joystickPosition = Offset.Zero
                            controlsViewModel.onJoystickMove(0f, 0f)
                        }) { _, dragAmount ->
                        val newPosition = joystickPosition + dragAmount
                        val distance =
                            sqrt(newPosition.x * newPosition.x + newPosition.y * newPosition.y)

                        joystickPosition = if (distance <= maxDistance) {
                            newPosition
                        } else {
                            val angle = atan2(newPosition.y, newPosition.x)
                            Offset(
                                cos(angle) * maxDistance, sin(angle) * maxDistance
                            )
                        }
                        controlsViewModel.onJoystickMove(
                            joystickPosition.x / maxDistance,
                            joystickPosition.y / maxDistance
                        )
                    }
                }) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF3A3A3A), darkGrey),
                    center = Offset(size.width * 0.3f, size.height * 0.3f)
                ), radius = joystickRadius, center = center
            )

            drawCircle(
                color = lightGrey,
                radius = joystickRadius,
                center = center,
                style = Stroke(width = 6f)
            )
            val stickCenter = center + joystickPosition
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF666666), Color(0xFF333333)), center = Offset(
                        stickCenter.x - stickRadius * 0.3f, stickCenter.y - stickRadius * 0.3f
                    )
                ), radius = stickRadius, center = stickCenter
            )

            drawCircle(
                color = Color(0xFF777777),
                radius = stickRadius,
                center = stickCenter,
                style = Stroke(width = 4f)
            )
        }
    }
}