package com.quangkhai.getgo_application.presentation.ui.friend.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.presentation.ui.main.components.MapSearchBar
import com.quangkhai.getgo_application.presentation.ui.main.components.SearchResults
import com.quangkhai.getgo_application.presentation.ui.main.components.OsmMapView
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.viewmodel.MapViewModel

@Composable
fun PickLocationSheet(
    visible: Boolean,
    onPicked: (Location) -> Unit,
    onDismiss: () -> Unit,
    mapViewModel: MapViewModel = viewModel()
) {
    val searchResults by mapViewModel.searchResults.collectAsState()
    val pickedLocation by mapViewModel.pickedLocation.collectAsState()

    val searchState = rememberTextFieldState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .collect { mapViewModel.onQueryChange(it) }
    }

    // start fresh every time the sheet opens
    LaunchedEffect(visible) {
        if (visible) {
            mapViewModel.clearPicked()
            searchState.setTextAndPlaceCursorAtEnd("")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // dim background, tap outside to cancel
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) { detectTapGestures { onDismiss() } }
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .shadow(16.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) { detectTapGestures { } }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        "Pick a location",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clipToBounds()
                ) {
                    OsmMapView(
                        pickedLocation = pickedLocation,
                        recenterTarget = null,
                        recenterKey = 0,
                        onMapTap = { _, _ -> focusManager.clearFocus() },
                        onMapLongPress = { latitude, longitude ->
                            focusManager.clearFocus()
                            mapViewModel.pinAt(latitude, longitude)
                        },
                        onPinMoved = { latitude, longitude -> mapViewModel.pinAt(latitude, longitude) },
                        onDeletePin = { mapViewModel.clearPicked() },
                        showPickedInfoWindow = true,
                        modifier = Modifier.fillMaxSize()
                    )

                    MapSearchBar(
                        state = searchState,
                        onSearch = { mapViewModel.search(searchState.text.toString()) },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    )

                    if (searchResults.isNotEmpty()) {
                        SearchResults(
                            results = searchResults,
                            onPickResult = { location ->
                                mapViewModel.pick(location)
                                val filledText = if (location.address.isBlank()) location.name
                                    else "${location.name} - ${location.address}"
                                searchState.setTextAndPlaceCursorAtEnd(filledText)
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 66.dp)
                        )
                    }
                }

                val place = pickedLocation
                if (place != null) {
                    PillButton(
                        text = "Use this location",
                        onClick = { onPicked(place) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(14.dp)
                    )
                }
            }
        }
    }
}
