package com.quangkhai.getgo_application.presentation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

enum class PillStyle { Primary, Outline, Danger }

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PillStyle = PillStyle.Primary
) {
    val blue = MaterialTheme.colorScheme.tertiary
    val red = GetGoTheme.colors.dangerous
    val surface = MaterialTheme.colorScheme.surface

    val background = if (style == PillStyle.Primary) blue else surface
    val contentColor = if (style == PillStyle.Danger) red
        else if (style == PillStyle.Outline) blue
        else Color.White
    val borderColor = if (style == PillStyle.Danger) red else blue

    Text(
        text = text,
        color = contentColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}
