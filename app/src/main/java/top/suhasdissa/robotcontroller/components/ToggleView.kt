package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.suhasdissa.robotcontroller.util.Pref
import top.suhasdissa.robotcontroller.util.rememberPreference

@Composable
fun ToggleView(
    view: Boolean,
    lightGrey: Color,
    darkGrey: Color,
    touchEnabled: Boolean = true,
) {
    val useLowLatency by rememberPreference(Pref.useLowLatencyStream, true)
    if (view) {
        if (useLowLatency) {
            LowLatencyStreamContainer(
                modifier = Modifier.Companion.fillMaxSize(),
                label = "CAMERA",
                lightGrey = lightGrey,
                darkGrey = darkGrey,
            )
        } else {
            StreamContainer(
                modifier = Modifier.Companion.fillMaxSize(),
                label = "CAMERA",
                lightGrey = lightGrey,
                darkGrey = darkGrey,
            )
        }
    } else {
        Box(
            modifier =
                Modifier.Companion
                    .fillMaxSize()
                    .background(darkGrey)
                    .border(2.dp, lightGrey, RoundedCornerShape(12.dp))
                    .shadow(
                        8.dp,
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(12.dp),
                    ).clip(
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(12.dp),
                    ),
            contentAlignment = Alignment.Companion.Center,
        ) {
            ArenaCoordinateMapper(touchEnabled = touchEnabled)
        }
    }
}
