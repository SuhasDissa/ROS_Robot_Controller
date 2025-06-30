package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    val selectedPoint by arenaMapViewModel.selectedPoint.observeAsState()
    val robotPosition by arenaMapViewModel.robotPosition.observeAsState()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(arenaWidthMeters / arenaHeightMeters)
        ) {
            Image(
                painter = painterResource(R.drawable.robocon_arena),
                contentDescription = "Robocon Arena",
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size
                    }
                    .then(
                        if (touchEnabled) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    if (imageSize != IntSize.Zero) {
                                        val realWorldX =
                                            (offset.x / imageSize.width) * arenaWidthMeters
                                        val realWorldY =
                                            (offset.y / imageSize.height) * arenaHeightMeters

                                        arenaMapViewModel.onCoordinateSelected(
                                            realWorldX,
                                            realWorldY
                                        )
                                    }
                                }
                            }
                        } else {
                            Modifier
                        }
                    ),
                contentScale = ContentScale.Fit
            )



            selectedPoint?.let { (x, y) ->
                val pixelX = (x / arenaWidthMeters) * imageSize.width
                val pixelY = (y / arenaHeightMeters) * imageSize.height

                Box(
                    modifier = Modifier
                        .offset(
                            x = with(LocalDensity.current) { pixelX.toDp() - 8.dp },
                            y = with(LocalDensity.current) { pixelY.toDp() - 8.dp }
                        )
                        .size(16.dp)
                        .background(
                            Color.Red,
                            CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            if (touchEnabled) {
                selectedPoint?.let { (x, y) ->
                    val pixelX = (x / arenaWidthMeters) * imageSize.width
                    val pixelY = (y / arenaHeightMeters) * imageSize.height

                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(LocalDensity.current) { pixelX.toDp() - 8.dp },
                                y = with(LocalDensity.current) { pixelY.toDp() - 8.dp }
                            )
                            .size(16.dp)
                            .background(
                                Color.Red,
                                CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape)
                    )
                }

                robotPosition?.let { (x, y, r) ->
                    val pixelX = (x / arenaWidthMeters) * imageSize.width
                    val pixelY = (y / arenaHeightMeters) * imageSize.height
                    // robot size is 0.8 meters
                    val robotSizePx = (0.8f / arenaWidthMeters) * imageSize.width
                    val robotSizeDp = with(LocalDensity.current) { robotSizePx.toDp() }

                    Image(
                        painterResource(R.drawable.robot_top), "Passer Robot",
                        modifier = Modifier
                            .offset(
                                x = with(LocalDensity.current) { pixelX.toDp() - robotSizeDp / 2 },
                                y = with(LocalDensity.current) { pixelY.toDp() - robotSizeDp / 2 }
                            )
                            .rotate(r)
                            .size(robotSizeDp))
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