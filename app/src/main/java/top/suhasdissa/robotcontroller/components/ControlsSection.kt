package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ControlsSection(
    modifier: Modifier = Modifier,
    darkGrey: Color,
    metalGrey: Color,
    lightGrey: Color,
    accentGreen: Color,
    accentRed: Color
) {
    Row(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(metalGrey, darkGrey),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ), shape = RoundedCornerShape(15.dp)
            )
            .border(2.dp, lightGrey, RoundedCornerShape(15.dp))
            .shadow(4.dp, RoundedCornerShape(15.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        JoystickControl(
            darkGrey = darkGrey, lightGrey = lightGrey
        )

        AngleControl(
            darkGrey = darkGrey, lightGrey = lightGrey, accentGreen = accentGreen
        )

        ButtonsAndSwitches(
            lightGrey = lightGrey, accentGreen = accentGreen, accentRed = accentRed
        )
    }
}