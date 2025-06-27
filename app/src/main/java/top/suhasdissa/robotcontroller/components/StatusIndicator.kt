package top.suhasdissa.robotcontroller.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusIndicator(
    label: String, isOnline: Boolean, accentGreen: Color, accentRed: Color
) {
    Row(
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp)
            )
            .border(1.dp, Color(0xFF444444), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (isOnline) accentGreen else accentRed, CircleShape
                )
                .shadow(
                    6.dp, CircleShape, spotColor = if (isOnline) accentGreen else accentRed
                )
        )
        Text(
            text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
    }
}