package top.suhasdissa.robotcontroller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.suhasdissa.robotcontroller.components.ControlsSection
import top.suhasdissa.robotcontroller.components.StatusIndicators
import top.suhasdissa.robotcontroller.components.StreamSection

@Composable
fun GameInterface(modifier: Modifier = Modifier) {
    val darkGrey = Color(0xFF1A1A1A)
    val metalGrey = Color(0xFF2D2D2D)
    val lightGrey = Color(0xFF444444)
    val accentGreen = Color(0xFF00FF88)
    val accentRed = Color(0xFFCC4444)

    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(metalGrey, darkGrey), center = Offset(0.3f, 0.3f)
                    )
                )
                .padding(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            StreamSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                lightGrey = lightGrey
            )

            Spacer(modifier = Modifier.height(16.dp))

            ControlsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                darkGrey = darkGrey,
                metalGrey = metalGrey,
                lightGrey = lightGrey,
                accentGreen = accentGreen,
                accentRed = accentRed
            )
        }

        StatusIndicators(
            modifier = Modifier.align(Alignment.TopEnd),
            accentGreen = accentGreen,
            accentRed = accentRed
        )
    }

}

@Preview()
@Composable
fun GameInterfacePreview() {
    GameInterface()
}