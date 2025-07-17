package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suhasdissa.robotcontroller.R
import top.suhasdissa.robotcontroller.viewmodels.ArenaMapViewModel

@Composable
fun ArenaCoordinateMapper(
    modifier: Modifier = Modifier,
    touchEnabled: Boolean
) {
    val arenaMapViewModel: ArenaMapViewModel = viewModel(factory = ArenaMapViewModel.Factory)
    val arenaWidthMeters = 15f
    val arenaHeightMeters = 8f
    val context = LocalContext.current

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val selectedPoint by arenaMapViewModel.selectedPoint.observeAsState()
    val robotPosition by arenaMapViewModel.robotPosition.observeAsState()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .aspectRatio(arenaWidthMeters / arenaHeightMeters)
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size
                }
                .then(
                    if (touchEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures { offset ->
                                if (canvasSize != IntSize.Zero) {
                                    val realWorldX =
                                        (offset.x / canvasSize.width) * arenaWidthMeters
                                    val realWorldY =
                                        (offset.y / canvasSize.height) * arenaHeightMeters
                                    arenaMapViewModel.onCoordinateSelected(realWorldX, realWorldY)
                                }
                            }
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            // Draw arena background
            val arenaDrawable = ContextCompat.getDrawable(context, R.drawable.robocon_arena)
            arenaDrawable?.let { drawable ->
                drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                drawable.draw(drawContext.canvas.nativeCanvas)
            }

            // Draw selected point
            selectedPoint?.let { (x, y) ->
                val pixelX = (x / arenaWidthMeters) * size.width
                val pixelY = (y / arenaHeightMeters) * size.height

                drawCircle(
                    color = Color.Red,
                    radius = 8.dp.toPx(),
                    center = Offset(pixelX, pixelY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 8.dp.toPx(),
                    center = Offset(pixelX, pixelY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            // Draw robot position
            if (touchEnabled) {
                robotPosition?.let { (x, y, rotation) ->
                    val pixelX = (x / arenaWidthMeters) * size.width
                    val pixelY = (y / arenaHeightMeters) * size.height
                    val robotSizePx = (0.8f / arenaWidthMeters) * size.width

                    val robotDrawable = ContextCompat.getDrawable(context, R.drawable.robot_top)
                    robotDrawable?.let { drawable ->
                        rotate(rotation, Offset(pixelX, pixelY)) {
                            drawable.setBounds(
                                (pixelX - robotSizePx / 2).toInt(),
                                (pixelY - robotSizePx / 2).toInt(),
                                (pixelX + robotSizePx / 2).toInt(),
                                (pixelY + robotSizePx / 2).toInt()
                            )
                            drawable.draw(drawContext.canvas.nativeCanvas)
                        }
                    }
                }
            }
        }

        Text(
            text = "ARENA",
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopStart)
                .background(
                    Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)
                )
                .border(1.dp, Color(0xFF555555), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}