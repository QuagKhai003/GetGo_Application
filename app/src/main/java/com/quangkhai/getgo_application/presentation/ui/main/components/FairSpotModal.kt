package com.quangkhai.getgo_application.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun FairSpotModal(
    friendNames: List<String>,
    onFindFairSpot: () -> Unit,
    onPickLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .pointerInput(Unit) { detectTapGestures { onDismiss() } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .pointerInput(Unit) { detectTapGestures { } }
                .padding(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Calculate Fair Spot",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(GetGoTheme.colors.dangerous)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = if (friendNames.isEmpty()) "No friends selected for the trip yet."
                    else "Trip friends: ${friendNames.joinToString(", ")}",
                color = GetGoTheme.colors.labelColor,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(14.dp))
            Text(
                "How would you like to find a spot?",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton("Find a fair spot", onClick = onFindFairSpot, modifier = Modifier.fillMaxWidth(), style = PillStyle.Primary)
                PillButton("Pick a location", onClick = onPickLocation, modifier = Modifier.fillMaxWidth(), style = PillStyle.Outline)
            }
        }
    }
}
