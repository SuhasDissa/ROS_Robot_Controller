package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicators(
    modifier: Modifier = Modifier, accentGreen: Color, accentRed: Color
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        StatusIndicator(
//            label = "CONNECTED", isOnline = true, accentGreen = accentGreen, accentRed = accentRed
//        )
//        StatusIndicator(
//            label = "SIGNAL: 85%",
//            isOnline = false,
//            accentGreen = accentGreen,
//            accentRed = accentRed
//        )
    }
}