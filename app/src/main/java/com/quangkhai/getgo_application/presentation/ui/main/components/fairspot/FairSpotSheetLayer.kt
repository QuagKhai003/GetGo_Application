package com.quangkhai.getgo_application.presentation.ui.main.components.fairspot
import com.quangkhai.getgo_application.presentation.ui.main.components.placedetail.PlaceDetailSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.usecase.map.haversineMeters

// The bottom sheet in fair-spot mode: once a spot is chosen, the member-distance box +
// the normal place sheet; before that, the "tap a fair spot" sheet.
@Composable
fun FairSpotSheetLayer(
    chosen: Location?,
    members: List<Pair<String, Location>>,
    places: List<Location>,
    loading: Boolean,
    circleVisible: Boolean,
    expanded: Boolean,
    sheetHeight: Dp,
    favorites: List<Location>,
    onExpandedChange: (Boolean) -> Unit,
    onToggleCircle: () -> Unit,
    onSaveFavorite: (Location) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onAddBill: (Location) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spot = chosen
    if (spot != null) {
        Column(modifier = modifier.fillMaxWidth()) {
            FairMemberBox(
                distances = members.map { it.first to haversineMeters(spot.lat, spot.long, it.second.lat, it.second.long) },
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
            val favorite = favorites.firstOrNull { it.lat == spot.lat && it.long == spot.long }
            PlaceDetailSheet(
                location = spot,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                isFavorite = favorite != null,
                onSave = { onSaveFavorite(spot) },
                onRemove = { favorite?.id?.let(onRemoveFavorite) },
                onAddBill = { onAddBill(spot) },
                onClose = onClose,
                circleVisible = circleVisible,
                onToggleCircle = onToggleCircle,
                modifier = Modifier.fillMaxWidth().height(sheetHeight)
            )
        }
    } else {
        FairSpotSheet(
            places = places,
            loading = loading,
            circleVisible = circleVisible,
            onToggleCircle = onToggleCircle,
            onClose = onClose,
            modifier = modifier.fillMaxWidth()
        )
    }
}
