package com.quangkhai.getgo_application.presentation.ui.main.components.fact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.quangkhai.getgo_application.domain.model.Fact
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// "Did you know?" card: a photo, the fact, and a Visit button that pins the place.
@Composable
fun FactModal(fact: Fact, onVisit: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (fact.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = fact.imageUrl,
                    contentDescription = fact.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Did you know?",
                        color = GetGoTheme.colors.labelColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "✕",
                        color = GetGoTheme.colors.dangerous,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Text(
                    fact.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (fact.description.isNotBlank()) {
                    Text(
                        fact.description.replaceFirstChar { it.uppercase() },
                        color = GetGoTheme.colors.labelColor,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Visit",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable { onVisit() }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                )
            }
        }
    }
}
