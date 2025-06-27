package top.suhasdissa.robotcontroller.components

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import top.suhasdissa.robotcontroller.viewmodels.StreamViewModel
import top.suhasdissa.robotcontroller.data.StreamState

@OptIn(UnstableApi::class)
@Composable
fun StreamContainer(
    modifier: Modifier = Modifier,
    label: String,
    lightGrey: Color,
    viewModel: StreamViewModel = viewModel()
) {
    val streamState by viewModel.streamState.observeAsState(StreamState.Loading)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2A2A2A), Color(0xFF1F1F1F)),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ), shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, lightGrey, RoundedCornerShape(12.dp))
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        when (val state = streamState) {
            is StreamState.Live -> {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            // Get the ExoPlayer from ViewModel
                            player = viewModel.exoPlayer
                            useController = false
                            setBackgroundColor(Color.Black.toArgb())
                        }
                    },
                    modifier = modifier.fillMaxSize()
                )
            }

            is StreamState.Loading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF888888),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "CONNECTING...",
                            color = Color(0xFF888888),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            is StreamState.Error -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "CONNECTION ERROR",
                            color = Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.message,
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
//                                if (state.stream != null) {
//                                    onStreamStateChange(StreamState.Live(state.stream))
//                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF444444)
                            )
                        ) {
                            Text("RETRY", color = Color.White)
                        }
                    }
                }
            }

            is StreamState.Offline -> {
                Box(modifier = modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val stripeWidth = 8f
                        var x = -size.height
                        while (x < size.width) {
                            drawLine(
                                color = Color(0xFF222222),
                                start = Offset(x, 0f),
                                end = Offset(x + size.height, size.height),
                                strokeWidth = stripeWidth
                            )
                            x += stripeWidth * 2
                        }
                    }

                    Text(
                        text = "NO STREAM AVAILABLE",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF888888),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Stream status indicator
        Text(
            text = when (streamState) {
                is StreamState.Live -> "LIVE"
                is StreamState.Loading -> "CONNECTING"
                is StreamState.Error -> "ERROR"
                is StreamState.Offline -> "OFFLINE"
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .background(
                    when (streamState) {
                        is StreamState.Live -> Color.Red.copy(alpha = 0.8f)
                        is StreamState.Loading -> Color.Yellow.copy(alpha = 0.8f)
                        is StreamState.Error -> Color.Red.copy(alpha = 0.8f)
                        is StreamState.Offline -> Color.Gray.copy(alpha = 0.8f)
                    },
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        // Label
        Text(
            text = label,
            modifier = Modifier
                .padding(10.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)
                )
                .border(1.dp, Color(0xFF555555), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}