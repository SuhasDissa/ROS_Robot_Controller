package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize.Fill
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suhasdissa.robotcontroller.viewmodels.ControlsViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AngleControl(
    darkGrey: Color,
    lightGrey: Color,
    accentGreen: Color
) {
    val controlsViewModel: ControlsViewModel = viewModel(factory = ControlsViewModel.Factory)
    var sliderValue by remember { mutableFloatStateOf(180f) }
    val angle = sliderValue.toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                controlsViewModel.onAngleChange(it)
            },
            valueRange = 0f..360f,
            modifier = Modifier.width(500.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF777777),
                activeTrackColor = lightGrey,
                inactiveTrackColor = darkGrey,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        Text(
            text = "${angle}°",
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2A2A2A), darkGrey)
                    ), shape = RoundedCornerShape(6.dp)
                )
                .border(2.dp, lightGrey, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = accentGreen,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RotaryAngleControl(
    darkGrey: Color,
    lightGrey: Color,
    accentGreen: Color
) {
    val controlsViewModel: ControlsViewModel = viewModel(factory = ControlsViewModel.Factory)
    var angle by remember { mutableFloatStateOf(60f) }
    var isDragging by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A1A), darkGrey),
                        radius = 400f
                    ),
                    shape = CircleShape
                )
                .border(
                    width = if (isDragging) 3.dp else 2.dp,
                    color = if (isDragging) accentGreen else lightGrey,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                isDragging = false
                            }
                        ) { change , _->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touchPoint = change.position

                            val deltaX = touchPoint.x - center.x
                            val deltaY = touchPoint.y - center.y

                            var newAngle = Math
                                .toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
                                .toFloat() + 90f

                            if (newAngle < 0) newAngle += 360f
                            if (newAngle >= 360f) newAngle -= 360f

                            angle = newAngle
                            controlsViewModel.onAngleChange(newAngle)
                        }
                    }
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 - 30.dp.toPx()

                drawCircle(
                    color = lightGrey.copy(alpha = 0.3f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                for (tickAngle in 0 until 360 step 10) {
                    val tickRadians = Math.toRadians((tickAngle - 90).toDouble())
                    val isMainTick = tickAngle % 30 == 0
                    val tickLength = if (isMainTick) 15.dp.toPx() else 8.dp.toPx()
                    val tickWidth = if (isMainTick) 2.dp.toPx() else 1.dp.toPx()

                    val startRadius = radius - tickLength
                    val startX = center.x + startRadius * cos(tickRadians).toFloat()
                    val startY = center.y + startRadius * sin(tickRadians).toFloat()
                    val endX = center.x + radius * cos(tickRadians).toFloat()
                    val endY = center.y + radius * sin(tickRadians).toFloat()

                    drawLine(
                        color = lightGrey,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = tickWidth
                    )
                }

                val angleRadians = Math.toRadians((angle - 90).toDouble())
                val armEndX = center.x + radius * cos(angleRadians).toFloat()
                val armEndY = center.y + radius * sin(angleRadians).toFloat()

                drawLine(
                    color = accentGreen,
                    start = center,
                    end = Offset(armEndX, armEndY),
                    strokeWidth = if (isDragging) 6.dp.toPx() else 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = if (isDragging) accentGreen else lightGrey,
                    radius = if (isDragging) 8.dp.toPx() else 6.dp.toPx(),
                    center = center
                )

                drawCircle(
                    color = accentGreen,
                    radius = if (isDragging) 8.dp.toPx() else 6.dp.toPx(),
                    center = Offset(armEndX, armEndY)
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = if (isDragging) 3.dp.toPx() else 2.dp.toPx(),
                    center = Offset(armEndX, armEndY)
                )
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(
                    text = "${angle.toInt()}°",
                    modifier = Modifier
                        .offset(y = 50.dp),
                    color = accentGreen,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}