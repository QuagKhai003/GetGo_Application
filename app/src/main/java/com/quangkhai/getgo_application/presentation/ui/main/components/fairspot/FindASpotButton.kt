package com.quangkhai.getgo_application.presentation.ui.main.components.fairspot
import android.annotation.SuppressLint
import com.quangkhai.getgo_application.presentation.ui.main.components.map.clickableOverMap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@SuppressLint("ModifierParameter")
@Composable
fun FindASpotButton(boredMode: Boolean = false, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = if (boredMode) "I'm Feeling Bored" else "Fair Spot!!!",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(GetGoTheme.colors.quaternary)
            .clickableOverMap { onClick() }
            .padding(horizontal = 28.dp, vertical = 12.dp)
    )
}
