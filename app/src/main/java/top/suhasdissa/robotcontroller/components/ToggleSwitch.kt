package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToggleSwitch(
    label: String,
    initialState: Boolean,
    accentGreen: Color
) {
    var isToggled by remember { mutableStateOf(initialState) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Switch(
            checked = isToggled,
            onCheckedChange = { isToggled = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentGreen,
                checkedTrackColor = accentGreen.copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFF666666),
                uncheckedTrackColor = Color(0xFF333333),
                checkedBorderColor = Color(0xFF444444),
                uncheckedBorderColor = Color(0xFF444444)
            )
        )

        Text(
            text = label.uppercase(),
            color = Color(0xFFBBBBBB),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}