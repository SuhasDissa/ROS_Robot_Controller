package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonsAndSwitches(
    lightGrey: Color, accentGreen: Color, accentRed: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton(
                text = "FIRE", accentRed = accentRed, lightGrey = lightGrey
            )
            ActionButton(
                text = "ARM", accentRed = accentRed, lightGrey = lightGrey
            )
            ActionButton(
                text = "ZOOM", accentRed = accentRed, lightGrey = lightGrey
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            ToggleSwitch(label = "Power", initialState = true, accentGreen = accentGreen)
            ToggleSwitch(label = "Auto", initialState = false, accentGreen = accentGreen)
            ToggleSwitch(label = "Toggle", initialState = false, accentGreen = accentGreen)
        }
    }
}