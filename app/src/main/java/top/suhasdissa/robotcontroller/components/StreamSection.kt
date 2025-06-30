package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StreamSection(
    modifier: Modifier = Modifier,
    isPrimaryFirst: Boolean,
    lightGrey: Color,
    darkGrey: Color
) {
    Box(
        modifier = modifier
    ) {
        ToggleView(!isPrimaryFirst, lightGrey, darkGrey)
    }
}

