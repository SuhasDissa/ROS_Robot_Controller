package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun StreamSection(
    modifier: Modifier = Modifier,
    lightGrey: Color,
    initialPipPosition: Offset = Offset(0f, 0f)
) {
    var isPrimaryFirst by remember { mutableStateOf(true) }
    var pipOffset by remember { mutableStateOf(initialPipPosition) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        val pipWidth = 200.dp
        val pipHeight = 150.dp
        val density = LocalDensity.current

        ToggleView(!isPrimaryFirst, lightGrey)

        Card(
            modifier = Modifier
                .size(pipWidth, pipHeight)
                .offset(
                    x = with(density) { pipOffset.x.toDp() },
                    y = with(density) { pipOffset.y.toDp() }
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                        }
                    ) { _, dragAmount ->
                        pipOffset = Offset(
                            x = pipOffset.x + dragAmount.x,
                            y = pipOffset.y + dragAmount.y
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (!isDragging) {
                                isPrimaryFirst = !isPrimaryFirst
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(12.dp)
        ) {
            ToggleView(isPrimaryFirst, lightGrey, touchEnabled = false)
        }
    }
}

@Composable
fun ToggleView(view: Boolean, lightGrey: Color, touchEnabled: Boolean = true) {
    if (view) {
        StreamContainer(
            modifier = Modifier.fillMaxSize(), label = "CAMERA", lightGrey = lightGrey
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2A2A2A), Color(0xFF1F1F1F)),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ), shape = RoundedCornerShape(12.dp)
                )
                .border(2.dp, lightGrey, RoundedCornerShape(12.dp))
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center
        ) {
            ArenaCoordinateMapper(touchEnabled = touchEnabled)
        }
    }
}