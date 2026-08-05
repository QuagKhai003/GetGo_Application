package com.quangkhai.getgo_application.presentation.ui.main.components.fairspot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.usecase.map.DiscoverCategory
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.ui.theme.GetGoTheme


// Claude Opus 4.8 generated code for open up category for circle search in find a spot button
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FairSpotCategoryDialog(
    categories: List<DiscoverCategory>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val selected = remember { mutableStateListOf<String>() }

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
                    "What kind of place?",
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

            Spacer(Modifier.height(14.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isOn = category.term in selected
                    Text(
                        text = category.label,
                        color = if (isOn) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isOn) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface)
                            .border(1.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50))
                            .clickable { if (isOn) selected.remove(category.term) else selected.add(category.term) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            PillButton(
                text = "Find spots",
                onClick = { if (selected.isNotEmpty()) onConfirm(selected.toList()) },
                modifier = Modifier.fillMaxWidth(),
                style = PillStyle.Primary
            )
        }
    }
}
