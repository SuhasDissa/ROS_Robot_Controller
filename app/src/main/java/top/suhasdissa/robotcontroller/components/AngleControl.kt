package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AngleControl(
    darkGrey: Color,
    lightGrey: Color,
    accentGreen: Color
) {
    var sliderValue by remember { mutableFloatStateOf(0.45f) }
    val angle = (sliderValue * 180).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..1f,
            modifier = Modifier.width(500.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF777777),
                activeTrackColor = lightGrey,
                inactiveTrackColor = darkGrey,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        Text(
            text = "${angle}°",
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2A2A2A), darkGrey)
                    ), shape = RoundedCornerShape(6.dp)
                )
                .border(2.dp, lightGrey, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = accentGreen,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}