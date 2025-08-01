package top.suhasdissa.robotcontroller.data

import androidx.compose.ui.graphics.Color

data class Doughnut(
    val center: Pair<Float, Float>,
    val innerRadius: Float,
    val outerRadius: Float,
    val color: Color
)
