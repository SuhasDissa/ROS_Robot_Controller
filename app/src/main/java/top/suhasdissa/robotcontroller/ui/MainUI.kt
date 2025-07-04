package top.suhasdissa.robotcontroller.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suhasdissa.robotcontroller.components.ControlsSection
import top.suhasdissa.robotcontroller.components.StatusDialog
import top.suhasdissa.robotcontroller.components.StatusIndicators
import top.suhasdissa.robotcontroller.components.StreamSection
import top.suhasdissa.robotcontroller.components.ToggleView
import top.suhasdissa.robotcontroller.util.rememberPreference
import top.suhasdissa.robotcontroller.viewmodels.ROSConnectViewModel

@Composable
fun GameInterface(modifier: Modifier = Modifier) {
    val darkGrey = Color(0xFF1A1A1A)
    val metalGrey = Color(0xFF2D2D2D)
    val lightGrey = Color(0xFF444444)
    val accentGreen = Color(0xFF00FF88)
    val accentRed = Color(0xFFCC4444)

    var controlsVisible by remember { mutableStateOf(true) }
    var isPrimaryFirst by remember { mutableStateOf(true) }

    val rosConnectViewModel: ROSConnectViewModel = viewModel(factory = ROSConnectViewModel.Factory)
    val rosConnectUiState by rosConnectViewModel.uiState.collectAsState()
    val connectionStatus by rosConnectViewModel.connectionStatus.collectAsState()

    var showConnectDialog by remember { mutableStateOf(false) }

    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(metalGrey, darkGrey), center = Offset(0.3f, 0.3f)
                    )
                )
                .padding(4.dp)
        ) {
            StreamSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                isPrimaryFirst = isPrimaryFirst,
                lightGrey = lightGrey,
                darkGrey = darkGrey
            )

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedVisibility(
                visible = controlsVisible,
                enter = expandVertically(animationSpec = tween(500))
            ) {
                ControlsSection(
                    modifier = Modifier
                        .fillMaxWidth(),
                    darkGrey = darkGrey,
                    lightGrey = lightGrey,
                    accentGreen = accentGreen,
                    accentRed = accentRed
                )
            }
        }
        StatusIndicators(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(true) {
                    showConnectDialog = true
                },
            rosConnectUiState
        )
    }

    StatusDialog(
        showDialog = showConnectDialog,
        connectionStatus = connectionStatus,
        uiState = rosConnectUiState,
        onConnect = { rosConnectViewModel.connect() },
        onDisconnect = { rosConnectViewModel.disconnect() }
    ) { showConnectDialog = false }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { controlsVisible = !controlsVisible },
            shape = CircleShape,
            containerColor = metalGrey,
            contentColor = Color.White,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = if (controlsVisible) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = if (controlsVisible) "Hide Controls" else "Show Controls"
            )
        }
    }

    var savedOffset by rememberPreference("pip_offset", Offset(0f, 0f))
    var pipOffset by remember { mutableStateOf(savedOffset) }
    var isDragging by remember { mutableStateOf(false) }
    val pipWidth = 200.dp
    val pipHeight = 150.dp
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .size(pipWidth, pipHeight)
            .offset(
                x = with(density) { pipOffset.x.toDp() },
                y = with(density) { pipOffset.y.toDp() }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        savedOffset = pipOffset
                    }
                ) { _, dragAmount ->
                    pipOffset = Offset(
                        x = pipOffset.x + dragAmount.x,
                        y = pipOffset.y + dragAmount.y
                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!isDragging) {
                            isPrimaryFirst = !isPrimaryFirst
                        }
                    }
                )
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        ToggleView(isPrimaryFirst, lightGrey, darkGrey, touchEnabled = false)
    }
}

@Preview()
@Composable
fun GameInterfacePreview() {
    GameInterface()
}