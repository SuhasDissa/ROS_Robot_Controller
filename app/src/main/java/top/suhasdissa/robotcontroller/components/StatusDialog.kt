package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import top.suhasdissa.robotcontroller.ui.EnhancedButton
import top.suhasdissa.robotcontroller.viewmodels.ROSConnectUiState

@Composable
fun StatusDialog(
    showDialog: Boolean,
    connectionStatus: Boolean,
    uiState: ROSConnectUiState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            StatusCard(
                connectionStatus = connectionStatus,
                uiState = uiState,
                onConnect = onConnect,
                onDisconnect = onDisconnect
            )
        }
    }
}

@Composable
fun StatusCard(
    connectionStatus: Boolean,
    uiState: ROSConnectUiState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val darkGrey = Color(0xFF1A1A1A)
    val lightGrey = Color(0xFF444444)
    val accentGreen = Color(0xFF66BB6A)
    val accentRed = Color(0xFFEF5350)

    val statusColor = if (connectionStatus) accentGreen else accentRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            darkGrey,
                            darkGrey.copy(alpha = 0.8f)
                        )
                    )
                )
                .border(
                    2.dp,
                    Brush.horizontalGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.6f),
                            lightGrey
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    statusColor.copy(alpha = 0.3f),
                                    statusColor.copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        )
                        .border(2.dp, statusColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (connectionStatus) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = "Connection Status",
                        modifier = Modifier.size(28.dp),
                        tint = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "ROS Bridge Connection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Text
                Text(
                    text = if (connectionStatus) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Message
                when (val state = uiState) {
                    is ROSConnectUiState.Connected -> {
                        Text(
                            text = state.statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }

                    is ROSConnectUiState.Disconnected -> {
                        Text(
                            text = state.statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }

                    is ROSConnectUiState.Error -> {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = accentRed,
                            textAlign = TextAlign.Center
                        )
                    }

                    is ROSConnectUiState.Loading -> {
                        Text(
                            text = state.statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button
                if (!connectionStatus) {
                    EnhancedButton(
                        text = "Connect",
                        icon = Icons.Default.PowerSettingsNew,
                        onClick = onConnect,
                        enabled = uiState !is ROSConnectUiState.Loading,
                        accentColor = accentGreen,
                        darkGrey = darkGrey,
                        lightGrey = lightGrey,
                        isLoading = uiState is ROSConnectUiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    EnhancedButton(
                        text = "Disconnect",
                        icon = Icons.Default.PowerOff,
                        onClick = onDisconnect,
                        enabled = uiState !is ROSConnectUiState.Loading,
                        accentColor = accentRed,
                        darkGrey = darkGrey,
                        lightGrey = lightGrey,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}