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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import top.suhasdissa.robotcontroller.R

@Composable
fun ArenaCoordinateMapper(
    modifier: Modifier = Modifier,
    touchEnabled: Boolean,
    onCoordinateSelected: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    val arenaWidthMeters = 15f
    val arenaHeightMeters = 8f

    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var selectedPoint by remember { mutableStateOf<Pair<Float, Float>?>(null) }

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

                                        selectedPoint = Pair(realWorldX, realWorldY)
                                        onCoordinateSelected(realWorldX, realWorldY)
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
        }
    }
}