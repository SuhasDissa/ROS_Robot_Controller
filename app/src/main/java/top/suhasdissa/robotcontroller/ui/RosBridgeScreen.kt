package top.suhasdissa.robotcontroller.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import top.suhasdissa.robotcontroller.data.ros.ROSMessage
import top.suhasdissa.robotcontroller.viewmodels.CommunicationTestViewModel
import top.suhasdissa.robotcontroller.viewmodels.CommunicationUiState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ROSBridgeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CommunicationTestViewModel = viewModel(factory = CommunicationTestViewModel.Factory)
) {
    val connectionStatus: Boolean by viewModel.connectionStatus.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val messageHistory = viewModel.messageHistory

    val darkGrey = Color(0xFF1A1A1A)
    val lightGrey = Color(0xFF444444)
    val accentBlue = Color(0xFF4FC3F7)
    val accentGreen = Color(0xFF66BB6A)
    val accentRed = Color(0xFFEF5350)
    val accentOrange = Color(0xFFFF9800)

    var messageText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        darkGrey,
                        Color(0xFF0D0D0D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        accentGreen.copy(alpha = 0.2f),
                                        accentGreen.copy(alpha = 0.05f)
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(accentGreen, accentGreen.copy(alpha = 0.3f))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = "Communication Tester",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Communication Tester",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Test ROS Bridge Connection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }

            // Enhanced Connection Status Card
            EnhancedStatusCard(
                connectionStatus = connectionStatus,
                uiState = uiState,
                accentGreen = accentGreen,
                accentRed = accentRed,
                darkGrey = darkGrey,
                lightGrey = lightGrey,
                onConnect = { viewModel.connectToROS() },
                onDisconnect = { viewModel.disconnect() }
            )

            EnhancedMessageInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        viewModel.publishMessage(messageText)
                        messageText = ""
                    }
                },
                connectionStatus = connectionStatus,
                accentBlue = accentBlue,
                darkGrey = darkGrey,
                lightGrey = lightGrey
            )

            EnhancedMessageHistory(
                messageHistory = messageHistory,
                onClearMessages = { viewModel.clearMessages() },
                accentOrange = accentOrange,
                darkGrey = darkGrey,
                lightGrey = lightGrey,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EnhancedStatusCard(
    connectionStatus: Boolean,
    uiState: CommunicationUiState,
    accentGreen: Color,
    accentRed: Color,
    darkGrey: Color,
    lightGrey: Color,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    val statusColor = if (connectionStatus) accentGreen else accentRed

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
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
                        modifier = Modifier.size(24.dp),
                        tint = statusColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ROS Bridge Connection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (connectionStatus) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    when (val state = uiState) {
                        is CommunicationUiState.Connected -> {
                            Text(
                                text = state.statusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }

                        is CommunicationUiState.Disconnected -> {
                            Text(
                                text = state.statusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }

                        is CommunicationUiState.Error -> {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = accentRed
                            )
                        }

                        is CommunicationUiState.Loading -> {
                            Text(
                                text = state.statusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (!connectionStatus) {
                    EnhancedButton(
                        text = "Connect",
                        icon = Icons.Default.PowerSettingsNew,
                        onClick = onConnect,
                        enabled = uiState !is CommunicationUiState.Loading,
                        accentColor = accentGreen,
                        darkGrey = darkGrey,
                        lightGrey = lightGrey,
                        isLoading = uiState is CommunicationUiState.Loading,
                        modifier = Modifier.width(150.dp) // Adjust width as needed
                    )
                } else {
                    EnhancedButton(
                        text = "Disconnect",
                        icon = Icons.Default.PowerOff,
                        onClick = onDisconnect,
                        enabled = uiState !is CommunicationUiState.Loading,
                        accentColor = accentRed,
                        darkGrey = darkGrey,
                        lightGrey = lightGrey,
                        modifier = Modifier.width(150.dp) // Adjust width as needed
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    accentColor: Color,
    darkGrey: Color,
    lightGrey: Color,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .scale(animatedScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.1f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                lightGrey.copy(alpha = 0.1f),
                                lightGrey.copy(alpha = 0.05f)
                            )
                        )
                    },
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    if (enabled) accentColor.copy(alpha = 0.4f) else lightGrey.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = accentColor
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) accentColor else lightGrey
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = if (enabled) Color.White else lightGrey,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedMessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    connectionStatus: Boolean,
    accentBlue: Color,
    darkGrey: Color,
    lightGrey: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            darkGrey.copy(alpha = 0.9f),
                            darkGrey
                        )
                    )
                )
                .border(
                    1.dp,
                    lightGrey.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        modifier = Modifier.size(20.dp),
                        tint = accentBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    label = { Text("Enter your message", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = connectionStatus,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentBlue,
                        unfocusedBorderColor = lightGrey.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = lightGrey,
                        cursorColor = accentBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                EnhancedButton(
                    text = "Send Message",
                    icon = Icons.Default.Send,
                    onClick = onSendMessage,
                    enabled = connectionStatus && messageText.isNotBlank(),
                    accentColor = accentBlue,
                    darkGrey = darkGrey,
                    lightGrey = lightGrey,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EnhancedMessageHistory(
    messageHistory: List<ROSMessage>,
    onClearMessages: () -> Unit,
    accentOrange: Color,
    darkGrey: Color,
    lightGrey: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            darkGrey.copy(alpha = 0.9f),
                            darkGrey
                        )
                    )
                )
                .border(
                    1.dp,
                    lightGrey.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Message History",
                            modifier = Modifier.size(20.dp),
                            tint = accentOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Messages (${messageHistory.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (messageHistory.isNotEmpty()) {
                        TextButton(
                            onClick = onClearMessages,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = accentOrange
                            )
                        ) {
                            Text("Clear", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    items(messageHistory.reversed()) { message ->
                        EnhancedMessageItem(
                            message = message,
                            accentOrange = accentOrange,
                            darkGrey = darkGrey,
                            lightGrey = lightGrey
                        )
                    }

                    if (messageHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Message,
                                        contentDescription = "No Messages",
                                        modifier = Modifier.size(48.dp),
                                        tint = lightGrey.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No messages received yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = lightGrey,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedMessageItem(
    message: ROSMessage,
    accentOrange: Color,
    darkGrey: Color,
    lightGrey: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    lightGrey.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    accentOrange.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.message.topic,
                        style = MaterialTheme.typography.labelMedium,
                        color = accentOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(java.util.Date(message.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message.message.msg.asString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}