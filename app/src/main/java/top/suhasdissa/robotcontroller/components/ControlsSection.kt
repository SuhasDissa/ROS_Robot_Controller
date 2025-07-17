package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun ControlsSection(
    modifier: Modifier = Modifier,
    darkGrey: Color,
    lightGrey: Color,
    accentGreen: Color,
    accentRed: Color
) {
    val configuration = LocalConfiguration.current
    val isPortrait =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            modifier = modifier
                .border(2.dp, lightGrey, RoundedCornerShape(15.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                JoystickControl(
//                    darkGrey = darkGrey, lightGrey = lightGrey
//                )

                DPadControl(darkGrey,lightGrey)

                RotaryAngleControl(
                    darkGrey = darkGrey, lightGrey = lightGrey, accentGreen = accentGreen
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ButtonsAndSwitches(
                    lightGrey = lightGrey,
                    accentGreen = accentGreen,
                    accentRed = accentRed
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .border(2.dp, lightGrey, RoundedCornerShape(15.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

//            JoystickControl(
//                darkGrey = darkGrey, lightGrey = lightGrey
//            )
            DPadControl(darkGrey,lightGrey)

            RotaryAngleControl(
                darkGrey = darkGrey, lightGrey = lightGrey, accentGreen = accentGreen
            )

            ButtonsAndSwitches(
                lightGrey = lightGrey,
                accentGreen = accentGreen,
                accentRed = accentRed
            )
        }
    }
}