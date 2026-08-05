package com.quangkhai.getgo_application.presentation.ui.main.components.fact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.R
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

// small round button above the main button that toggles the mode
@Composable
fun BoredSwitchCircle(onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .clickableOverMap { onToggle() }
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_swap_horiz_24),
            contentDescription = "switch mode",
            tint = Color.White,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BoredSwitchCirclePreview() {
    GetGo_ApplicationTheme {
        BoredSwitchCircle(onToggle = {})
    }
}
