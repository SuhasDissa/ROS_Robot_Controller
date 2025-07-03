package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import top.suhasdissa.robotcontroller.viewmodels.ROSConnectUiState

@Composable
fun StatusIndicators(
    modifier: Modifier = Modifier,
    state: ROSConnectUiState
) {
    val accentBlue = Color(0xFF4FC3F7)
    val accentGreen = Color(0xFF66BB6A)
    val accentRed = Color(0xFFEF5350)
    val accentOrange = Color(0xFFFF9800)

    Box(modifier = modifier) {
        when (state) {
            is ROSConnectUiState.Connected -> {
                StatusIndicator(
                    label = "CONNECTED",
                    background = accentGreen
                )
            }

            is ROSConnectUiState.Disconnected -> {
                StatusIndicator(
                    label = "DISCONNECTED",
                    background = accentOrange
                )
            }

            is ROSConnectUiState.Loading -> {
                StatusIndicator(
                    label = "CONNECTING...",
                    background = accentBlue
                )
            }

            is ROSConnectUiState.Error -> {
                StatusIndicator(
                    label = "ERROR",
                    background = accentRed
                )
            }

        }
    }
}