package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(arenaWidthMeters / arenaHeightMeters),
            //contentAlignment = Alignment.Center
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

                    Image(
                        painterResource(R.drawable.robot_top), "Passer Robot",
                        modifier = Modifier
                            .offset(
                                x = with(LocalDensity.current) { pixelX.toDp() - 8.dp },
                                y = with(LocalDensity.current) { pixelY.toDp() - 8.dp }
                            )
                            .rotate(r)
                            .size(50.dp))
                }
            }
        }
    }
}