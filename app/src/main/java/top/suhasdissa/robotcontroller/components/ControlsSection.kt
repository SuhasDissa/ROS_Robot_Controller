package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ControlsSection(
    modifier: Modifier = Modifier,
    darkGrey: Color,
    lightGrey: Color,
    accentGreen: Color,
    accentRed: Color
) {
    Row(
        modifier = modifier
            .border(2.dp, lightGrey, RoundedCornerShape(15.dp)),
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