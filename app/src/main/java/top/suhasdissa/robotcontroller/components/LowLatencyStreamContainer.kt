package top.suhasdissa.robotcontroller.components

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexvas.rtsp.widget.RtspSurfaceView // Import RtspSurfaceView
import top.suhasdissa.robotcontroller.data.LowLatencyStreamState
import top.suhasdissa.robotcontroller.viewmodels.LowLatencyStreamViewModel

@Composable
fun LowLatencyStreamContainer(
    modifier: Modifier = Modifier,
    label: String,
    lightGrey: Color,
    darkGrey: Color,
    viewModel: LowLatencyStreamViewModel = viewModel(),
) {
    val lowLatencyStreamState by viewModel.lowLatencyStreamState.observeAsState(
        LowLatencyStreamState.Offline,
    )

    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .background(darkGrey)
                .border(2.dp, lightGrey, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
    ) {
        AndroidView(
            factory = {
                RtspSurfaceView(it).apply {
                    setStatusListener(viewModel.getRtspStatusListener())
                    viewModel.startStream(this)
                }
            },
            modifier = Modifier.fillMaxSize(),
            onRelease = { view ->
                if (view.isStarted()) {
                    viewModel.stopStream(view)
                }
            },
        )
        when (val state = lowLatencyStreamState) {
            is LowLatencyStreamState.Loading, is LowLatencyStreamState.Connecting -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF888888),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text =
                                when (state) {
                                    is LowLatencyStreamState.Loading -> "LOADING..."
                                    is LowLatencyStreamState.Connecting -> "CONNECTING..."
                                    else -> "CONNECTING..." // Fallback
                                },
                            color = Color(0xFF888888),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            is LowLatencyStreamState.Disconnecting -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF888888),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "DISCONNECTING...",
                            color = Color(0xFF888888),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            is LowLatencyStreamState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp),
                        )
                        Text(
                            text = "CONNECTION ERROR",
                            color = Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = state.message,
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }

            is LowLatencyStreamState.Offline -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Diagonal stripes pattern
                    Canvas(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        val stripeWidth = 8f
                        var x = -size.height
                        while (x < size.width) {
                            drawLine(
                                color = Color(0xFF222222),
                                start = Offset(x, 0f),
                                end = Offset(x + size.height, size.height),
                                strokeWidth = stripeWidth,
                            )
                            x += stripeWidth * 2
                        }
                    }

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "NO STREAM AVAILABLE",
                            color = Color(0xFF888888),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            else -> Unit
        }

        // Stream status indicator
        Text(
            text =
                when (lowLatencyStreamState) {
                    is LowLatencyStreamState.Live -> "LIVE"
                    is LowLatencyStreamState.Loading -> "LOADING"
                    is LowLatencyStreamState.Connecting -> "CONNECTING"
                    is LowLatencyStreamState.Disconnecting -> "DISCONNECTING"
                    is LowLatencyStreamState.Error -> "ERROR"
                    is LowLatencyStreamState.Offline -> "OFFLINE"
                },
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(
                        when (lowLatencyStreamState) {
                            is LowLatencyStreamState.Live -> Color.Red.copy(alpha = 0.8f)
                            is LowLatencyStreamState.Loading, is LowLatencyStreamState.Connecting ->
                                Color.Yellow.copy(
                                    alpha = 0.8f,
                                )

                            is LowLatencyStreamState.Disconnecting -> Color.Yellow.copy(alpha = 0.8f)
                            is LowLatencyStreamState.Error -> Color.Red.copy(alpha = 0.8f)
                            is LowLatencyStreamState.Offline -> Color.Gray.copy(alpha = 0.8f)
                        },
                        RoundedCornerShape(4.dp),
                    ).padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )

        // Label
        Text(
            text = label,
            modifier =
                Modifier
                    .padding(10.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(4.dp),
                    ).border(1.dp, Color(0xFF555555), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
