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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import top.suhasdissa.robotcontroller.util.Pref
import top.suhasdissa.robotcontroller.util.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val darkGrey = Color(0xFF1A1A1A)
    Color(0xFF444444)
    val accentBlue = Color(0xFF4FC3F7)
    val accentGreen = Color(0xFF66BB6A)
    val accentOrange = Color(0xFFFF9800)

    var showWebsocketDialog by remember { mutableStateOf(false) }
    var showRtspDialog by remember { mutableStateOf(false) }

    var rtspUrl by rememberPreference(Pref.RTSPURLKey, Pref.DefaultRTSPURL)
    var websocketUrl by rememberPreference(Pref.WebsocketURLKey, Pref.DefaultWebsocketURL)

    Box(
        modifier = Modifier
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
                .padding(20.dp)
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
                                        accentOrange.copy(alpha = 0.2f),
                                        accentOrange.copy(alpha = 0.05f)
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
                                    colors = listOf(accentOrange, accentOrange.copy(alpha = 0.3f))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Configure Connection Settings",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SettingCard(
                        title = "RTSP Stream URL",
                        description = "Configure video stream endpoint",
                        value = rtspUrl,
                        icon = Icons.Default.Videocam,
                        accentColor = accentBlue,
                        onClick = { showRtspDialog = true }
                    )
                }

                item {
                    SettingCard(
                        title = "WebSocket URL",
                        description = "Configure control communication endpoint",
                        value = websocketUrl,
                        icon = Icons.Default.Wifi,
                        accentColor = accentGreen,
                        onClick = { showWebsocketDialog = true }
                    )
                }
            }
        }
    }

    // RTSP URL Dialog
    if (showRtspDialog) {
        UrlInputDialog(
            title = "RTSP Stream URL",
            currentValue = rtspUrl,
            placeholder = "rtsp://192.168.1.100:554/stream",
            accentColor = accentBlue,
            onSave = { url ->
                rtspUrl = url
                showRtspDialog = false
            },
            onDismiss = { showRtspDialog = false }
        )
    }

    // WebSocket URL Dialog
    if (showWebsocketDialog) {
        UrlInputDialog(
            title = "WebSocket URL",
            currentValue = websocketUrl,
            placeholder = "ws://192.168.1.100:8080",
            accentColor = accentGreen,
            onSave = { url ->
                websocketUrl = url
                showWebsocketDialog = false
            },
            onDismiss = { showWebsocketDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingCard(
    title: String,
    description: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val darkGrey = Color(0xFF1A1A1A)
    val lightGrey = Color(0xFF444444)
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "setting_card_scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
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
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.6f),
                            lightGrey
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            // Background accent glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(0.2f, 0.5f),
                            radius = 800f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with animated background
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.2f),
                                        accentColor.copy(alpha = 0.05f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                accentColor.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(28.dp),
                            tint = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            lineHeight = 18.sp
                        )
                    }

                    // Edit icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                lightGrey.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp),
                            tint = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current value display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            lightGrey.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            accentColor.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = value.ifEmpty { "Not configured" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (value.isEmpty()) Color.Gray else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlInputDialog(
    title: String,
    currentValue: String,
    placeholder: String,
    accentColor: Color,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember { mutableStateOf(currentValue) }
    val darkGrey = Color(0xFF1A1A1A)
    val lightGrey = Color(0xFF444444)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = darkGrey,
        tonalElevation = 8.dp,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.2f),
                                    accentColor.copy(alpha = 0.05f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = accentColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Enter the URL for the connection:",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = {
                        Text(
                            text = "URL",
                            color = accentColor.copy(alpha = 0.7f)
                        )
                    },
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = lightGrey,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        cursorColor = accentColor
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(textValue) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = accentColor
                )
            ) {
                Text(
                    text = "Save",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.LightGray
                )
            ) {
                Text("Cancel")
            }
        }
    )
}