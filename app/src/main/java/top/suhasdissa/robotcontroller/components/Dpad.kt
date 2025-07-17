package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suhasdissa.robotcontroller.viewmodels.ControlsViewModel

enum class DPadDirection(val key: String) {
    UP("w"), DOWN("s"), LEFT("a"), RIGHT("d"), CENTER("c")
}

@Composable
fun DPadControl(
    darkGrey: Color,
    lightGrey: Color,
    onDirectionPressed: (DPadDirection) -> Unit = {}
) {
    val controlsViewModel: ControlsViewModel = viewModel(factory = ControlsViewModel.Factory)
    var pressedDirection by remember { mutableStateOf<DPadDirection>(DPadDirection.CENTER) }
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Up button
            DPadButton(
                direction = DPadDirection.UP,
                isPressed = pressedDirection == DPadDirection.UP,
                darkGrey = darkGrey,
                lightGrey = lightGrey,
                haptics = haptics,
                onPressed = { direction ->
                    pressedDirection = direction
                    onDirectionPressed(direction)
                    direction?.let { controlsViewModel.onDPadPressed(it) }
                        ?: controlsViewModel.onDPadReleased()
                }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left button
                DPadButton(
                    direction = DPadDirection.LEFT,
                    isPressed = pressedDirection == DPadDirection.LEFT,
                    darkGrey = darkGrey,
                    lightGrey = lightGrey,
                    haptics = haptics,
                    onPressed = { direction ->
                        pressedDirection = direction
                        onDirectionPressed(direction)
                        direction?.let { controlsViewModel.onDPadPressed(it) }
                            ?: controlsViewModel.onDPadReleased()
                    }
                )

                DPadButton(
                    direction = DPadDirection.CENTER,
                    isPressed = pressedDirection == DPadDirection.CENTER,
                    darkGrey = darkGrey,
                    lightGrey = lightGrey,
                    haptics = haptics,
                    onPressed = { direction ->
                        pressedDirection = direction
                        onDirectionPressed(direction)
                        direction?.let { controlsViewModel.onDPadPressed(it) }
                            ?: controlsViewModel.onDPadReleased()
                    }
                )

                // Right button
                DPadButton(
                    direction = DPadDirection.RIGHT,
                    isPressed = pressedDirection == DPadDirection.RIGHT,
                    darkGrey = darkGrey,
                    lightGrey = lightGrey,
                    haptics = haptics,
                    onPressed = { direction ->
                        pressedDirection = direction
                        onDirectionPressed(direction)
                        direction?.let { controlsViewModel.onDPadPressed(it) }
                            ?: controlsViewModel.onDPadReleased()
                    }
                )
            }

            // Down button
            DPadButton(
                direction = DPadDirection.DOWN,
                isPressed = pressedDirection == DPadDirection.DOWN,
                darkGrey = darkGrey,
                lightGrey = lightGrey,
                haptics = haptics,
                onPressed = { direction ->
                    pressedDirection = direction
                    onDirectionPressed(direction)
                    direction?.let { controlsViewModel.onDPadPressed(it) }
                        ?: controlsViewModel.onDPadReleased()
                }
            )
        }
    }
}

@Composable
private fun DPadButton(
    direction: DPadDirection,
    isPressed: Boolean,
    darkGrey: Color,
    lightGrey: Color,
    haptics: HapticFeedback,
    onPressed: (DPadDirection) -> Unit
) {
    Button(
        onClick = { onPressed(direction) },
        modifier = Modifier
            .size(60.dp)
//            .pointerInput(direction) {
//                detectDragGestures(
//                    onDragStart = {
//                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                        onPressed(direction)
//                    },
//                    onDragEnd = { onPressed(null) },
//                    onDrag = { _, _ -> /* Keep direction pressed during drag */ }
//                )
//            }
//            .pointerInput(direction) {
//                detectTapGestures(
//                    onPress = {
//                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                        onPressed(direction)
//                        tryAwaitRelease()
//                        onPressed(null)
//                    }
//                )
//            }
        ,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) {
                Color(0xFF555555)
            } else {
                Color(0xFF666666)
            }
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isPressed) Color(0xFF888888) else lightGrey
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPressed) 2.dp else 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        DirectionArrowIcon(
            direction = direction,
            color = lightGrey
        )
    }
}

@Composable
private fun DirectionArrowIcon(
    direction: DPadDirection,
    color: Color
) {
    val iconRotation = when (direction) {
        DPadDirection.UP -> 0f
        DPadDirection.RIGHT -> 90f
        DPadDirection.DOWN -> 180f
        DPadDirection.LEFT -> 270f
        else -> 0f
    }
    if(direction != DPadDirection.CENTER) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "D-pad $direction",
            tint = color,
            modifier = Modifier.rotate(iconRotation)
        )
    }
}